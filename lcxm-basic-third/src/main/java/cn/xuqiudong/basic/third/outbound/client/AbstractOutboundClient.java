package cn.xuqiudong.basic.third.outbound.client;

import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.builder.OutboundRequestInfoBuilder;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 出站第三方 Client 基类。
 *
 * <p>本类只维护 HTTP 请求构建和执行的公共流程，不持有配置及执行器状态。
 * 运行时对象的加载、缓存和刷新由具体 Partner 统一管理。</p>
 *
 * @author Vic.xu
 * @since 2026-07-29
 */
public abstract class AbstractOutboundClient {

    /**
     * 创建不持有运行时状态的出站 Client 基类。
     *
     * <p>构造阶段不加载配置或创建执行器，第一次访问运行时信息时由子类完成延迟初始化。</p>
     */
    protected AbstractOutboundClient() {
    }

    /**
     * 获取当前 Client 使用的 HTTP 配置。
     *
     * <p>配置必须与 {@link #executor()} 来自同一个运行时快照。</p>
     *
     * @return 当前 HTTP 配置
     */
    protected abstract ThirdClientOptions options();

    /**
     * 获取当前 Client 使用的出站执行器。
     *
     * <p>执行器必须与 {@link #options()} 来自同一个运行时快照。</p>
     *
     * @return 当前出站执行器
     */
    protected abstract OutboundExecutor executor();

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
        return Collections.emptyMap();
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
        return executor().executeBytes(OutboundRequestInfo.builder(thirdIdentity())
                .method(ThirdHttpMethod.GET)
                .url(buildUrl(path))
                .headers(mergedHeaders())
                .build());
    }

    /**
     * 发起 POST 请求并返回原始字节。
     */
    protected byte[] postBytes(String path, Object body) {
        return executor().executeBytes(OutboundRequestInfo.builder(thirdIdentity())
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
        return executor().execute(request);
    }

    /**
     * 执行已完整构建的出站请求并返回原始字节。
     */
    protected byte[] executeBytes(OutboundRequestInfo<?> request) {
        return executor().executeBytes(request);
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
        Map<String, String> headers = new LinkedHashMap<>(options().getDefaultHeaders());
        headers.putAll(buildHeaders());
        return headers;
    }

}
