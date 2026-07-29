package cn.xuqiudong.basic.third.outbound.client;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.config.provider.DefaultThirdOptionsProvider;
import cn.xuqiudong.basic.third.config.provider.ThirdOptionsProvider;
import cn.xuqiudong.basic.third.outbound.builder.OutboundRequestInfoBuilder;
import cn.xuqiudong.basic.third.outbound.executor.HttpOutboundExecutor;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 出站第三方 Client 基类。
 *
 * <p>业务项目通常一个第三方写一个子类，在子类中实现厂商 URL、header 和具体业务方法。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractOutboundClient {

    private final ThirdOptionsProvider optionsProvider;

    private final OutboundExecutor executor;

    /**
     * 使用默认出站配置和 Hutool HTTP 执行器。
     */
    protected AbstractOutboundClient() {
        this(new DefaultThirdOptionsProvider(), null);
    }

    /**
     * 使用项目传入的配置 provider 和执行器。
     */
    protected AbstractOutboundClient(ThirdOptionsProvider optionsProvider, OutboundExecutor executor) {
        this.optionsProvider = optionsProvider == null ? new DefaultThirdOptionsProvider() : optionsProvider;
        this.executor = executor == null
                ? new HttpOutboundExecutor(this.optionsProvider.getOptions(thirdIdentity().getCode()))
                : executor;
    }

    /**
     * 当前第三方标识，用于配置读取和日志记录。
     */
    protected abstract ThirdIdentity thirdIdentity();

    /**
     * 将业务 path 拼成完整第三方 URL。
     */
    protected abstract String buildUrl(String path);

    /**
     * 构建当前第三方请求头；返回值会覆盖默认 header 中同名项。
     */
    protected Map<String, String> buildHeaders() {
        return Map.of();
    }

    /**
     * 发起 GET 请求并按 Class 解析响应。
     */
    protected <T> T get(String path, Class<T> responseType) {
        return execute(builder(responseType).method(ThirdHttpMethod.GET).url(buildUrl(path)).build());
    }

    /**
     * 发起 GET 请求并按 TypeReference 解析泛型响应。
     */
    protected <T> T get(String path, TypeReference<T> responseTypeReference) {
        return execute(OutboundRequestInfo.<T>builder(thirdIdentity())
                .method(ThirdHttpMethod.GET)
                .url(buildUrl(path))
                .headers(mergedHeaders())
                .responseType(responseTypeReference)
                .build());
    }

    /**
     * 发起 POST JSON 请求并按 Class 解析响应。
     */
    protected <T> T post(String path, Object body, Class<T> responseType) {
        return execute(builder(responseType).method(ThirdHttpMethod.POST).url(buildUrl(path)).body(body).build());
    }

    /**
     * 发起 GET 请求并返回原始字节，适合文件下载。
     */
    protected byte[] getBytes(String path) {
        return executor.executeBytes(OutboundRequestInfo.builder(thirdIdentity())
                .method(ThirdHttpMethod.GET)
                .url(buildUrl(path))
                .headers(mergedHeaders())
                .build());
    }

    /**
     * 发起 POST 请求并返回原始字节。
     */
    protected byte[] postBytes(String path, Object body) {
        return executor.executeBytes(OutboundRequestInfo.builder(thirdIdentity())
                .method(ThirdHttpMethod.POST)
                .url(buildUrl(path))
                .headers(mergedHeaders())
                .body(body)
                .build());
    }

    /**
     * 执行已完整构建的出站请求。
     */
    protected <T> T execute(OutboundRequestInfo<T> request) {
        return executor.execute(request);
    }

    /**
     * 创建带默认 thirdIdentity/header/responseType 的请求构建器。
     */
    protected <T> OutboundRequestInfoBuilder<T> builder(Class<T> responseType) {
        return OutboundRequestInfo.<T>builder(thirdIdentity())
                .headers(mergedHeaders())
                .responseType(responseType);
    }

    /**
     * 合并全局默认 header 和当前 Client header。
     */
    protected Map<String, String> mergedHeaders() {
        Map<String, String> headers = new LinkedHashMap<>(
                optionsProvider.getOptions(thirdIdentity().getCode()).getDefaultHeaders());
        headers.putAll(buildHeaders());
        return headers;
    }
}
