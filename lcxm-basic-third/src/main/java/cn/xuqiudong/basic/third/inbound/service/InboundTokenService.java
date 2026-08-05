package cn.xuqiudong.basic.third.inbound.service;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.inbound.config.InboundAppConfig;
import cn.xuqiudong.basic.third.inbound.model.TokenApplyRequest;
import cn.xuqiudong.basic.third.inbound.model.TokenValue;
import cn.xuqiudong.basic.third.inbound.registry.InboundAppConfigRegistry;
import cn.xuqiudong.basic.third.inbound.store.NonceStore;
import cn.xuqiudong.basic.third.inbound.store.TokenStore;
import cn.xuqiudong.basic.third.security.RsaSignatureUtils;
import cn.xuqiudong.basic.third.security.SignaturePayloadBuilder;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 第三方入站 token 服务。
 *
 * <p>按旧模块思路接收多个 {@link InboundAppConfigRegistry}，新增第三方时只新增 registry，
 * token 签发和校验逻辑不需要修改。</p>
 *
 * @author Vic.xu
 */
public class InboundTokenService {

    private static final Duration DEFAULT_CONFIG_CACHE_TTL = Duration.ofMinutes(5);

    private final Map<String, InboundAppConfigRegistry> registryByAppId;

    private final Cache<String, InboundAppConfig> configCache;

    private final Duration configCacheTtl;

    private final TokenStore tokenStore;

    private final NonceStore nonceStore;

    /**
     * 创建入站 token 服务。
     *
     * @param registries 第三方入站配置注册点，一个第三方通常一个 registry
     * @param tokenStore token 存储实现
     * @param nonceStore nonce 防重存储
     */
    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required service collaborators.")
    public InboundTokenService(Collection<InboundAppConfigRegistry> registries, TokenStore tokenStore,
            NonceStore nonceStore) {
        this(registries, tokenStore, nonceStore, DEFAULT_CONFIG_CACHE_TTL);
    }

    /**
     * 创建入站 token 服务。
     *
     * @param registries 第三方入站配置注册点，一个第三方通常一个 registry
     * @param tokenStore token 存储实现
     * @param nonceStore nonce 防重存储
     * @param configCacheTtl app 配置短缓存时间；小于等于 0 表示不缓存
     */
    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required service collaborators.")
    public InboundTokenService(Collection<InboundAppConfigRegistry> registries, TokenStore tokenStore,
            NonceStore nonceStore, Duration configCacheTtl) {
        if (CollUtil.isEmpty(registries)) {
            throw new IllegalArgumentException("registries can not be empty");
        }
        if (tokenStore == null) {
            throw new IllegalArgumentException("tokenStore can not be null");
        }
        this.registryByAppId = buildRegistryMap(registries);
        this.configCacheTtl = configCacheTtl == null ? DEFAULT_CONFIG_CACHE_TTL : configCacheTtl;
        this.configCache = buildConfigCache(this.configCacheTtl);
        this.tokenStore = tokenStore;
        this.nonceStore = nonceStore;
    }

    /**
     * 校验第三方签名并签发 token。
     */
    public String issueToken(TokenApplyRequest request) {
        String invalidMessage = validateBasicRequest(request);
        if (invalidMessage != null) {
            throw new ThirdException(invalidMessage);
        }
        InboundAppConfig config = getConfig(request.getAppId());
        invalidMessage = validateConfigAndRequest(config, request);
        if (invalidMessage != null) {
            throw new ThirdException(invalidMessage);
        }
        String payload = SignaturePayloadBuilder.buildTokenPayload(request);
        boolean verified = RsaSignatureUtils.publicVerify(request.getSign(), payload, config.getPublicKey());
        if (!verified) {
            throw new ThirdException("invalid sign");
        }
        if (config.isNonceRequired() && nonceStore != null) {
            boolean saved = nonceStore.saveIfAbsent(request.getAppId(), request.getNonce(),
                    toSeconds(config.getTimestampTolerance()));
            if (!saved) {
                throw new ThirdException("repeated nonce");
            }
        }
        String token = IdUtil.simpleUUID();
        Duration ttl = config.getTokenTtl();
        TokenValue value = new TokenValue(config.getAppId(), config.getThirdCode(), request.getUsername(), expireAt(ttl));
        tokenStore.put(token, value, toSeconds(ttl));
        return token;
    }

    /**
     * 校验 token 是否存在且仍在有效期内。
     */
    public TokenValue checkToken(String token) {
        if (StrUtil.isBlank(token)) {
            throw new ThirdException("blank token");
        }
        TokenValue tokenValue = tokenStore.get(token);
        if (tokenValue == null) {
            throw new ThirdException("invalid token");
        }
        if (tokenValue.isExpired()) {
            tokenStore.remove(token);
            throw new ThirdException("expired token");
        }
        return tokenValue;
    }

    /**
     * 主动注销 token。
     */
    public TokenValue revokeToken(String token) {
        TokenValue tokenValue = checkToken(token);
        tokenStore.remove(token);
        return tokenValue;
    }

    /**
     * 计算 token 过期时间。
     */
    private Date expireAt(Duration ttl) {
        return DateUtil.date(DateUtil.current() + ttl.toMillis());
    }

    /**
     * Store 层使用秒作为 TTL 单位，兼容低版本 Spring Data Redis。
     */
    private long toSeconds(Duration ttl) {
        long seconds = ttl.getSeconds();
        return seconds > 0 ? seconds : 1;
    }

    /**
     * 获取 app 配置；缓存过期后重新调用 registry.inboundConfig()。
     */
    private InboundAppConfig getConfig(String appId) {
        InboundAppConfigRegistry registry = registryByAppId.get(appId);
        if (registry == null) {
            return null;
        }
        if (isCacheDisabled()) {
            return loadConfig(registry);
        }
        return configCache.get(appId, key -> loadConfig(registry));
    }

    /**
     * 从 registry 重新加载 app 配置。
     */
    private InboundAppConfig loadConfig(InboundAppConfigRegistry registry) {
        InboundAppConfig config = registry.inboundConfig();
        validateRegistry(registry, config);
        config.setThirdCode(StrUtil.blankToDefault(config.getThirdCode(), registry.thirdCode()));
        return config;
    }

    /**
     * 将多个第三方 registry 初始化为 appId 索引，避免每次申请 token 时循环查找。
     *
     * <p>这里只固定 appId 和 registry 的关系；具体 InboundAppConfig 内容仍按短缓存从 registry 重新读取。</p>
     */
    private Map<String, InboundAppConfigRegistry> buildRegistryMap(Collection<InboundAppConfigRegistry> registries) {
        Map<String, InboundAppConfigRegistry> result = new LinkedHashMap<>();
        for (InboundAppConfigRegistry registry : registries) {
            if (registry == null) {
                throw new IllegalArgumentException("registry can not be null");
            }
            InboundAppConfig config = loadConfig(registry);
            InboundAppConfigRegistry old = result.putIfAbsent(config.getAppId(), registry);
            if (old != null) {
                throw new IllegalArgumentException("duplicate inbound appId: " + config.getAppId());
            }
        }
        return result;
    }

    private Cache<String, InboundAppConfig> buildConfigCache(Duration ttl) {
        Duration actualTtl = ttl == null || ttl.isZero() || ttl.isNegative() ? DEFAULT_CONFIG_CACHE_TTL : ttl;
        return Caffeine.newBuilder()
                .expireAfterWrite(actualTtl)
                .build();
    }

    private boolean isCacheDisabled() {
        return configCacheTtl.isZero() || configCacheTtl.isNegative();
    }

    /**
     * 校验 registry 的基础配置，提前暴露 appId/publicKey 等配置错误。
     */
    private void validateRegistry(InboundAppConfigRegistry registry, InboundAppConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("inboundConfig can not be null: " + registry.thirdCode());
        }
        if (StrUtil.isBlank(config.getAppId())) {
            throw new IllegalArgumentException("appId can not be blank: " + registry.thirdCode());
        }
    }

    /**
     * 校验申请 token 的必填请求字段。
     */
    private String validateBasicRequest(TokenApplyRequest request) {
        if (request == null) {
            return "request can not be null";
        }
        if (StrUtil.isBlank(request.getAppId())) {
            return "appId can not be blank";
        }
        if (StrUtil.isBlank(request.getUsername())) {
            return "username can not be blank";
        }
        if (StrUtil.isBlank(request.getSign())) {
            return "sign can not be blank";
        }
        return null;
    }

    /**
     * 校验 app 配置、时间窗、nonce、username 等非签名字段。
     */
    private String validateConfigAndRequest(InboundAppConfig config, TokenApplyRequest request) {
        if (config == null) {
            return "unknown appId";
        }
        if (StrUtil.isBlank(config.getPublicKey())) {
            return "publicKey can not be blank";
        }
        if (config.getTokenTtl() == null || config.getTokenTtl().isNegative() || config.getTokenTtl().isZero()) {
            return "tokenTtl must be positive";
        }
        if (config.getTimestampTolerance() == null
                || config.getTimestampTolerance().isNegative()
                || config.getTimestampTolerance().isZero()) {
            return "timestampTolerance must be positive";
        }
        long toleranceMillis = config.getTimestampTolerance().toMillis();
        if (Math.abs(DateUtil.current() - request.getTimestamp()) > toleranceMillis) {
            return "request timestamp out of tolerance";
        }
        if (config.isNonceRequired() && StrUtil.isBlank(request.getNonce())) {
            return "nonce can not be blank";
        }
        if (config.isNonceRequired() && nonceStore == null) {
            return "nonceStore can not be null when nonce required";
        }
        if (!config.allowsUsername(request.getUsername())) {
            return "invalid username";
        }
        return null;
    }
}
