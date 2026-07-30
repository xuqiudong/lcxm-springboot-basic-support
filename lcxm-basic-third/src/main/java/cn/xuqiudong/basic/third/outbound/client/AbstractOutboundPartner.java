package cn.xuqiudong.basic.third.outbound.client;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.api.ThirdApi;
import cn.xuqiudong.basic.third.outbound.builder.OutboundRequestInfoBuilder;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfig;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfigProvider;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import com.fasterxml.jackson.databind.JavaType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 第三方厂商出站 Client 基类。
 *
 * <p>本类在 {@link AbstractOutboundClient} 的 HTTP 执行底座上，增加配置获取、host 拼接、
 * API 枚举和常用请求封装。具体厂商的 token、签名、响应成功判断仍由子类实现。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractOutboundPartner<C extends OutboundPartnerConfig> extends AbstractOutboundClient {

    private final OutboundPartnerConfigProvider<C> configProvider;

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for invalid partner config provider.")
    protected AbstractOutboundPartner(OutboundPartnerConfigProvider<C> configProvider) {
        this(configProvider, new ThirdClientOptions(), null);
    }

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for invalid partner config provider.")
    protected AbstractOutboundPartner(OutboundPartnerConfigProvider<C> configProvider, ThirdClientOptions options,
            OutboundExecutor executor) {
        super(options, executor);
        if (configProvider == null) {
            throw new IllegalArgumentException("configProvider can not be null");
        }
        this.configProvider = configProvider;
    }

    /**
     * 获取当前第三方配置。
     */
    protected C getConfig() {
        C config = configProvider.getConfig(thirdIdentity());
        if (config == null) {
            throw new ThirdException("outbound partner config can not be null: " + thirdIdentity().getCode());
        }
        return config;
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
     * GET 请求。
     */
    protected <R> R get(ThirdApi api, Class<R> responseType) {
        return execute(api, builder(api, responseType).build());
    }

    /**
     * GET 请求。
     */
    protected <R> R get(ThirdApi api, JavaType responseJavaType) {
        return execute(api, this.<R>builder(api, responseJavaType).build());
    }

    /**
     * 默认 JSON 请求，适合大多数第三方 POST 接口。
     */
    protected <R> R request(ThirdApi api, Object body, Class<R> responseType) {
        return postJson(api, body, responseType);
    }

    /**
     * 默认 JSON 请求，支持 Jackson JavaType 泛型响应。
     */
    protected <R> R request(ThirdApi api, Object body, JavaType responseJavaType) {
        return postJson(api, body, responseJavaType);
    }

    /**
     * POST JSON 请求。
     */
    protected <R> R postJson(ThirdApi api, Object body, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api, ThirdHttpMethod.POST))
                .jsonBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * POST JSON 请求。
     */
    protected <R> R postJson(ThirdApi api, Object body, JavaType responseJavaType) {
        OutboundRequestInfo<R> request = this.<R>builder(api, responseJavaType)
                .method(resolveMethod(api, ThirdHttpMethod.POST))
                .jsonBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * POST form 请求。
     */
    protected <R> R postForm(ThirdApi api, Map<String, ?> formParams, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api, ThirdHttpMethod.POST))
                .formParams(formParams)
                .build();
        return execute(api, request);
    }

    /**
     * POST text 请求。
     */
    protected <R> R postText(ThirdApi api, String body, Class<R> responseType) {
        OutboundRequestInfo<R> request = builder(api, responseType)
                .method(resolveMethod(api, ThirdHttpMethod.POST))
                .textBody(body)
                .build();
        return execute(api, request);
    }

    /**
     * 返回字节数组，适合文件下载。
     */
    protected byte[] getBytes(ThirdApi api) {
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
                .method(resolveMethod(api, ThirdHttpMethod.GET))
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

    private ThirdHttpMethod resolveMethod(ThirdApi api, ThirdHttpMethod defaultMethod) {
        return api.getMethod() == null ? defaultMethod : api.getMethod();
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
