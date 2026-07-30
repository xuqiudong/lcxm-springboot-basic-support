package cn.xuqiudong.basic.third.outbound.client;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.api.ThirdApi;
import cn.xuqiudong.basic.third.outbound.builder.OutboundRequestInfoBuilder;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfig;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import com.fasterxml.jackson.databind.JavaType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 第三方厂商出站 Client 基类。
 *
 * <p>本类在 {@link AbstractOutboundClient} 的 HTTP 执行底座上，增加配置获取、host 拼接、
 * API 枚举和常用请求封装。具体厂商的 token、签名、响应成功判断仍由子类实现。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractOutboundPartner<C extends OutboundPartnerConfig> extends AbstractOutboundClient {

    private static final String CONFIG_CACHE_KEY = "config";

    private static final Duration DEFAULT_CONFIG_CACHE_TTL = Duration.ofMinutes(5);

    private final Cache<String, C> configCache;

    protected AbstractOutboundPartner() {
        this(new ThirdClientOptions(), null);
    }

    protected AbstractOutboundPartner(ThirdClientOptions options, OutboundExecutor executor) {
        super(options, executor);
        this.configCache = buildConfigCache();
    }

    /**
     * 获取当前第三方配置。
     *
     * <p>默认使用 Caffeine 短缓存，减少每次请求重复读取配置。配置模型本身不作为 Spring bean。</p>
     */
    protected final C getConfig() {
        Duration ttl = configCacheTtl();
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            return checkConfig(loadConfig());
        }
        return checkConfig(configCache.get(CONFIG_CACHE_KEY, key -> loadConfig()));
    }

    /**
     * 加载当前第三方配置。
     *
     * <p>具体项目可在这里读取数据库、Redis、配置中心，或调用项目自己的配置服务。</p>
     */
    protected abstract C loadConfig();

    /**
     * 第三方配置缓存时间。
     *
     * <p>返回小于等于 0 的时间表示不缓存。</p>
     */
    protected Duration configCacheTtl() {
        return DEFAULT_CONFIG_CACHE_TTL;
    }

    /**
     * 清理当前第三方配置缓存。
     */
    protected void clearConfigCache() {
        configCache.invalidate(CONFIG_CACHE_KEY);
    }

    /**
     * 根据 API 枚举解析真实 path。
     *
     * <p>默认直接使用 {@link ThirdApi#getPath()}。如果枚举中存的是配置项编码，子类覆盖本方法读取真实路径。</p>
     */
    protected String resolveApiPath(ThirdApi api) {
        return api.getPath();
    }

    /**
     * 将 host 和 API path 拼成完整 URL。
     */
    protected String buildUrl(ThirdApi api) {
        if (api == null) {
            throw new ThirdException("third api can not be null");
        }
        return joinUrl(getConfig().getHost(), resolveApiPath(api));
    }

    /**
     * 兼容父类 path 调用方式。
     */
    @Override
    protected String buildUrl(String path) {
        return joinUrl(getConfig().getHost(), path);
    }

    /**
     * 构建 API 级请求头；子类通常在这里追加 token、签名、幂等键等。
     */
    protected Map<String, String> buildHeaders(ThirdApi api) {
        return Collections.emptyMap();
    }

    @Override
    protected Map<String, String> buildHeaders() {
        return buildHeaders(null);
    }

    /**
     * 创建厂商 API 请求构建器。
     */
    protected <R> OutboundRequestInfoBuilder<R> builder(ThirdApi api, Class<R> responseType) {
        return this.<R>baseBuilder(api).responseType(responseType);
    }

    /**
     * 创建厂商 API 请求构建器。
     */
    protected <R> OutboundRequestInfoBuilder<R> builder(ThirdApi api, JavaType responseJavaType) {
        return this.<R>baseBuilder(api).responseJavaType(responseJavaType);
    }

    /**
     * 默认 JSON 请求，HTTP method 由 {@link ThirdApi#getMethod()} 决定。
     */
    protected <R> R request(ThirdApi api, Object body, Class<R> responseType) {
        return requestJson(api, body, responseType);
    }

    /**
     * 默认 JSON 请求，支持 Jackson JavaType 泛型响应。
     */
    protected <R> R request(ThirdApi api, Object body, JavaType responseJavaType) {
        return requestJson(api, body, responseJavaType);
    }

    /**
     * JSON 请求，HTTP method 由 {@link ThirdApi#getMethod()} 决定。
     */
    protected <R> R requestJson(ThirdApi api, Object body, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api))
                .jsonBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * JSON 请求，HTTP method 由 {@link ThirdApi#getMethod()} 决定。
     */
    protected <R> R requestJson(ThirdApi api, Object body, JavaType responseJavaType) {
        OutboundRequestInfo<R> request = this.<R>builder(api, responseJavaType)
                .method(resolveMethod(api))
                .jsonBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * form 请求，HTTP method 由 {@link ThirdApi#getMethod()} 决定。
     */
    protected <R> R requestForm(ThirdApi api, Map<String, ?> formParams, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api))
                .formParams(formParams)
                .build();
        return execute(api, request);
    }

    /**
     * text 请求，HTTP method 由 {@link ThirdApi#getMethod()} 决定。
     */
    protected <R> R requestText(ThirdApi api, String body, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api))
                .textBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * 字节数组响应，适合文件下载。
     */
    protected byte[] requestBytes(ThirdApi api) {
        OutboundRequestInfo<?> request = this.<Object>baseBuilder(api).build();
        return executeBytes(request);
    }

    /**
     * 执行请求并调用厂商响应钩子。
     */
    protected <R> R execute(ThirdApi api, OutboundRequestInfo<R> request) {
        R response = execute(request);
        afterResponse(api, response);
        return response;
    }

    /**
     * 厂商响应钩子；子类可在这里统一校验成功码或抛出业务异常。
     */
    protected <R> void afterResponse(ThirdApi api, R response) {
        // default no-op
    }

    private <R> OutboundRequestInfoBuilder<R> baseBuilder(ThirdApi api) {
        return OutboundRequestInfo.<R>builder(thirdIdentity())
                .operation(api.getApiName())
                .method(resolveMethod(api))
                .url(buildUrl(api))
                .headers(mergedHeaders(api))
                .timeout(getConfig().getClientOptions().getRequestTimeout());
    }

    private Map<String, String> mergedHeaders(ThirdApi api) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.putAll(options().getDefaultHeaders());
        headers.putAll(getConfig().getClientOptions().getDefaultHeaders());
        headers.putAll(buildHeaders(api));
        return headers;
    }

    private C checkConfig(C config) {
        if (config == null) {
            throw new ThirdException("outbound partner config can not be null: " + thirdIdentity().getCode());
        }
        return config;
    }

    private Cache<String, C> buildConfigCache() {
        Duration ttl = configCacheTtl();
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            ttl = DEFAULT_CONFIG_CACHE_TTL;
        }
        return Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(1)
                .build();
    }

    private ThirdHttpMethod resolveMethod(ThirdApi api) {
        if (api.getMethod() == null) {
            throw new ThirdException("third api method can not be null: " + api.getApiName());
        }
        return api.getMethod();
    }

    private String joinUrl(String host, String path) {
        if (StrUtil.isBlank(host)) {
            throw new ThirdException("outbound partner host can not be blank: " + thirdIdentity().getCode());
        }
        if (StrUtil.isBlank(path)) {
            return host;
        }
        boolean hostEndsWithSlash = host.endsWith("/");
        boolean pathStartsWithSlash = path.startsWith("/");
        if (hostEndsWithSlash && pathStartsWithSlash) {
            return host + path.substring(1);
        }
        if (!hostEndsWithSlash && !pathStartsWithSlash) {
            return host + "/" + path;
        }
        return host + path;
    }
}
