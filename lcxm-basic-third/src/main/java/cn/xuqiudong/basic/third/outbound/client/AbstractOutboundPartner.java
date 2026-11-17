package cn.xuqiudong.basic.third.outbound.client;

import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.api.ThirdApi;
import cn.xuqiudong.basic.third.outbound.builder.OutboundParams;
import cn.xuqiudong.basic.third.outbound.builder.OutboundRequestInfoBuilder;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfig;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutorFactory;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import cn.xuqiudong.basic.third.outbound.util.OutboundUrlUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 第三方厂商出站 Client 基类。
 *
 * <p>本类在 {@link AbstractOutboundClient} 的 HTTP 执行底座上，增加厂商配置读取、host 拼接、
 * 具体 API 枚举、默认 header 合并、Runtime 缓存和常用请求封装。</p>
 *
 * <p>使用场景：</p>
 * <p>1. 简单请求：直接使用 requestJson/requestForm/requestMultipart/requestText/requestBytes 这类终结方法。</p>
 * <p>2. 参数较多但结构单一：项目侧先用 Map 或自己的参数对象组织参数，再传给终结方法。</p>
 * <p>3. 混合 query/header/form/multipart/parser/timeout 等复杂请求：从 builder(api, responseType) 开始，
 * build 后调用 execute(api, request)。</p>
 *
 * <p>具体厂商的 token、签名、业务成功码判断、特殊响应处理仍由子类实现。</p>
 *
 * @author Vic.xu
 * @since 2026-07-29
 */
public abstract class AbstractOutboundPartner<C extends OutboundPartnerConfig, A extends ThirdApi>
        extends AbstractOutboundClient {

    /**
     * 单个 Partner 运行时快照的固定缓存键。
     */
    private static final String RUNTIME_CACHE_KEY = "runtime";

    /**
     * 未指定缓存时间时使用的默认有效期。
     */
    private static final Duration DEFAULT_CONFIG_CACHE_TTL = Duration.ofMinutes(5);

    /**
     * 当前 Partner 的运行时快照缓存，同一时刻只保存一个有效快照。
     *
     * <p>缓存本身也采用延迟初始化，避免构造父类时调用子类覆盖的 {@link #configCacheTtl()}。</p>
     */
    private volatile Cache<String, OutboundPartnerRuntime<C>> runtimeCache;

    /**
     * 使用系统级 HTTP 组件和日志策略创建 Partner 执行器的工厂。
     */
    private final OutboundExecutorFactory executorFactory;

    /**
     * 使用全局出站执行器工厂创建当前 partner 的执行器。
     *
     * <p>HTTP 配置统一从 {@link #loadConfig()} 返回的 {@link OutboundPartnerConfig#getClientOptions()} 获取。</p>
     */
    protected AbstractOutboundPartner(OutboundExecutorFactory executorFactory) {
        super();
        this.executorFactory = executorFactory;
    }

    /**
     * 从本次加载的厂商配置中解析 HTTP 配置。
     *
     * <p>显式传入配置可以避免 Runtime 创建过程中通过 {@link #getConfig()} 递归访问尚未完成的缓存。</p>
     *
     * @param config 本次加载的厂商配置
     * @return 对应的 HTTP 配置
     */
    protected ThirdClientOptions resolveOptions(C config) {
        return config.getClientOptions();
    }

    /**
     * 使用 HTTP 配置创建当前 Partner 的出站执行器。
     *
     * @param options 当前 Runtime 的 HTTP 配置
     * @return 与配置绑定的出站执行器
     */
    protected OutboundExecutor resolveExecutor(ThirdClientOptions options) {
        if (executorFactory == null) {
            throw new ThirdException("outbound executor factory can not be null");
        }
        return executorFactory.create(options);
    }

    /**
     * 获取当前第三方配置。
     *
     * <p>配置与 HTTP 配置、执行器来自同一个 Runtime 快照，三者统一创建和失效。</p>
     *
     * @return 当前 Runtime 中的厂商配置
     */
    protected final C getConfig() {
        return currentRuntime().getConfig();
    }

    /**
     * 获取当前 Runtime 中的 HTTP 配置。
     *
     * @return 当前 HTTP 配置
     */
    @Override
    protected final ThirdClientOptions options() {
        return currentRuntime().getOptions();
    }

    /**
     * 获取当前 Runtime 中的出站执行器。
     *
     * @return 当前出站执行器
     */
    @Override
    protected final OutboundExecutor executor() {
        return currentRuntime().getExecutor();
    }

    /**
     * 加载当前第三方配置。
     *
     * <p>具体项目可在这里读取数据库、Redis、配置中心，或调用项目自己的配置服务。</p>
     */
    protected abstract C loadConfig();

    /**
     * 第三方 Runtime 缓存时间。
     *
     * <p>返回 {@code null} 时使用默认 5 分钟；返回正数时按指定时间自动过期；
     * 返回小于等于 0 的时间时 Runtime 固定且不自动过期，只能通过 {@link #clearRuntimeCache()} 刷新。</p>
     *
     * @return Runtime 缓存有效期
     */
    protected Duration configCacheTtl() {
        return DEFAULT_CONFIG_CACHE_TTL;
    }

    /**
     * 清理当前 Partner 的完整 Runtime 缓存。
     *
     * <p>本方法不会中断已经持有旧 Runtime 的请求；下一次访问会延迟加载配置并创建新执行器。</p>
     */
    protected void clearRuntimeCache() {
        Cache<String, OutboundPartnerRuntime<C>> currentCache = runtimeCache;
        if (currentCache != null) {
            currentCache.invalidate(RUNTIME_CACHE_KEY);
        }
    }

    /**
     * 根据 API 枚举解析真实 path。
     *
     * <p>默认直接使用 {@link ThirdApi#getPath()}。如果枚举中存的是配置项编码，子类覆盖本方法读取真实路径。</p>
     */
    protected String resolveApiPath(A api) {
        return api.getPath();
    }

    /**
     * 将 host 和 API path 拼成完整 URL。
     */
    protected String buildUrl(A api) {
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

    protected String joinUrl(String host, String path) {
        return OutboundUrlUtils.joinHostPath(host, path, thirdIdentity().getCode());
    }

    /**
     * 构建 API 级请求头；子类通常在这里追加 token、签名、幂等键等。
     */
    protected Map<String, String> buildHeaders(A api) {
        return Collections.emptyMap();
    }

    @Override
    protected Map<String, String> buildHeaders() {
        return Collections.emptyMap();
    }

    /**
     * 创建厂商 API 请求构建器。
     *
     * <p>当 requestJson/requestForm/requestMultipart 等终结方法不满足时，从这里开始按需追加
     * query、header、form、multipart、responseParser、timeout 等参数，再调用 {@link #execute(ThirdApi, OutboundRequestInfo)}。</p>
     */
    protected <R> OutboundRequestInfoBuilder<R> builder(A api, Class<R> responseType) {
        return this.<R>baseBuilder(api).responseType(responseType);
    }

    /**
     * 创建厂商 API 请求构建器，支持 Jackson JavaType 泛型响应。
     *
     * <p>复杂请求的高级入口；用途同 {@link #builder(ThirdApi, Class)}。</p>
     */
    protected <R> OutboundRequestInfoBuilder<R> builder(A api, JavaType responseJavaType) {
        return this.<R>baseBuilder(api).responseJavaType(responseJavaType);
    }

    /**
     * 创建普通 query/form 参数构建器。
     *
     * <p>仅用于普通键值参数；文件上传和 header 请使用 request builder 或 buildHeaders。</p>
     */
    protected OutboundParams createParams() {
        return OutboundParams.create();
    }

    /**
     * JSON 请求终结方法，HTTP method 由 {@link ThirdApi#getMethod()} 决定。
     *
     * <p>适合只有 JSON body 的简单请求；如果还需要 query/header/parser 等，使用 builder(api, responseType)。</p>
     */
    protected <R> R requestJson(A api, Object body, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api))
                .jsonBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * JSON 请求终结方法，支持 Jackson JavaType 泛型响应。
     */
    protected <R> R requestJson(A api, Object body, JavaType responseJavaType) {
        OutboundRequestInfo<R> request = this.<R>builder(api, responseJavaType)
                .method(resolveMethod(api))
                .jsonBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * form 请求终结方法，HTTP method 由 {@link ThirdApi#getMethod()} 决定。
     *
     * <p>适合只有 form 参数的简单请求；参数需要逐项追加或混合 query/header 时，使用 builder(api, responseType)。</p>
     */
    protected <R> R requestForm(A api, Map<String, ?> formParams, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api))
                .formParams(formParams)
                .build();
        return execute(api, request);
    }

    /**
     * multipart 文件上传终结方法，文件来自本地 File。
     *
     * <p>适合单文件简单上传；如果需要 query、普通 multipart 字段、多个文件或特殊响应解析，使用 builder(api, responseType)。</p>
     */
    protected <R> R requestMultipart(A api, String fieldName, File file, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api))
                .multipartFile(fieldName, file)
                .build();
        return execute(api, request);
    }

    /**
     * multipart 文件上传终结方法，文件来自输入流。
     *
     * <p>正常执行到 multipart 写入时，Hutool 会在读取后关闭 inputStream；
     * 如果请求在写入前失败，调用方仍应自行兜底关闭。</p>
     */
    protected <R> R requestMultipart(A api, String fieldName, String fileName, InputStream inputStream,
                                     Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api))
                .multipartFile(fieldName, fileName, inputStream)
                .build();
        return execute(api, request);
    }

    /**
     * text 请求终结方法，HTTP method 由 {@link ThirdApi#getMethod()} 决定。
     */
    protected <R> R requestText(A api, String body, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api))
                .textBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * 字节数组响应终结方法，适合文件下载。
     */
    protected byte[] requestBytes(A api) {
        OutboundRequestInfo<?> request = this.<Object>baseBuilder(api).build();
        return executeBytes(request);
    }

    /**
     * 执行请求并调用厂商响应钩子。
     */
    protected <R> R execute(A api, OutboundRequestInfo<R> request) {
        R response = execute(request);
        afterResponse(api, response);
        return response;
    }

    /**
     * 厂商响应钩子；子类可在这里统一校验成功码或抛出业务异常。
     */
    protected <R> void afterResponse(A api, R response) {
        // default no-op
    }

    /**
     * 构建厂商请求的公共基础信息。
     *
     * <p>这里统一写入 thirdIdentity、operation、method、url、headers、timeout；
     * 子类通常使用 builder(api, responseType)，只有要自定义 builder 创建流程时才覆盖或调用本方法。</p>
     */
    protected <R> OutboundRequestInfoBuilder<R> baseBuilder(A api) {
        return OutboundRequestInfo.<R>builder(thirdIdentity())
                .operation(api.getApiName())
                .method(resolveMethod(api))
                .url(buildUrl(api))
                .headers(mergedHeaders(api))
                .timeout(options().getRequestTimeout());
    }

    /**
     * 合并当前 Runtime 的默认 Header 和 API 级 Header。
     *
     * @param api 当前 API
     * @return 合并后的请求 Header
     */
    protected Map<String, String> mergedHeaders(A api) {
        Map<String, String> headers = new LinkedHashMap<>(options().getDefaultHeaders());
        headers.putAll(buildHeaders(api));
        return headers;
    }

    /**
     * 校验配置加载结果，避免不完整 Runtime 进入缓存。
     *
     * @param config 待校验的厂商配置
     * @return 非空厂商配置
     */
    private C checkConfig(C config) {
        if (config == null) {
            throw new ThirdException("outbound partner config can not be null: " + thirdIdentity().getCode());
        }
        return config;
    }

    /**
     * 创建 Runtime 缓存。
     *
     * <p>非正数 TTL 表示固定 Runtime，因此不配置自动过期策略；Runtime 仍在第一次访问时延迟创建。</p>
     *
     * @return 空的 Runtime 缓存
     */
    private Cache<String, OutboundPartnerRuntime<C>> buildRuntimeCache() {
        Duration ttl = configCacheTtl();
        if (ttl == null) {
            ttl = DEFAULT_CONFIG_CACHE_TTL;
        }
        Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(1);
        if (!ttl.isZero() && !ttl.isNegative()) {
            builder.expireAfterWrite(ttl);
        }
        return builder.build();
    }

    /**
     * 获取当前运行时快照，缓存未命中时原子加载一个完整 Runtime。
     *
     * @return 当前 Partner Runtime
     */
    private OutboundPartnerRuntime<C> currentRuntime() {
        return runtimeCache().get(RUNTIME_CACHE_KEY, key -> createRuntime());
    }

    /**
     * 延迟创建并安全发布 Runtime 缓存。
     *
     * <p>双重检查确保并发首次访问只构建一个缓存，同时保证 TTL 在子类构造完成后再解析。</p>
     *
     * @return 当前 Partner 的 Runtime 缓存
     */
    private Cache<String, OutboundPartnerRuntime<C>> runtimeCache() {
        Cache<String, OutboundPartnerRuntime<C>> currentCache = runtimeCache;
        if (currentCache != null) {
            return currentCache;
        }
        synchronized (this) {
            currentCache = runtimeCache;
            if (currentCache == null) {
                currentCache = buildRuntimeCache();
                runtimeCache = currentCache;
            }
        }
        return currentCache;
    }

    /**
     * 按固定顺序创建完整运行时快照。
     *
     * <p>先加载厂商配置，再从同一配置解析 HTTP 参数，最后用该参数创建执行器。
     * 只有三个步骤全部成功后，Caffeine 才会将 Runtime 放入缓存。</p>
     *
     * @return 新创建的 Partner Runtime
     */
    private OutboundPartnerRuntime<C> createRuntime() {
        C config = checkConfig(loadConfig());
        ThirdClientOptions options = resolveOptions(config);
        OutboundExecutor executor = resolveExecutor(options);
        return new OutboundPartnerRuntime<>(config, options, executor);
    }

    protected ThirdHttpMethod resolveMethod(A api) {
        if (api.getMethod() == null) {
            throw new ThirdException("third api method can not be null: " + api.getApiName());
        }
        return api.getMethod();
    }

}
