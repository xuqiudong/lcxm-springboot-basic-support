package cn.xuqiudong.basic.third.outbound.model;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.outbound.builder.OutboundRequestInfoBuilder;
import cn.xuqiudong.basic.third.outbound.parser.OutboundResponseParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import lombok.Getter;

/**
 * 出站请求不可变模型。
 *
 * @author Vic.xu
 */
@Getter
public class OutboundRequestInfo<T> {

    /**
     * 第三方标识，用于配置读取和交换日志。
     */
    private final ThirdIdentity thirdIdentity;

    /**
     * 业务操作名，用于日志、排查和链路定位。
     */
    private final String operation;

    /**
     * 完整请求 URL。
     */
    private final String url;

    /**
     * HTTP method。
     */
    private final ThirdHttpMethod method;

    /**
     * 请求 header。
     */
    private final Map<String, String> headers;

    /**
     * query 参数。
     */
    private final Map<String, String> queryParams;

    /**
     * form 参数。
     */
    private final Map<String, String> formParams;

    /**
     * 请求体类型；默认执行器按此决定请求体写入方式和 Content-Type。
     */
    private final OutboundRequestType requestType;

    /**
     * 请求 body；默认执行器会将非 String 对象序列化为 JSON。
     */
    private final Object body;

    /**
     * 本次请求超时时间；为空时使用第三方默认配置。
     */
    private final Duration timeout;

    /**
     * 响应 Class 类型。
     */
    private final Class<T> responseType;

    /**
     * 泛型响应类型。
     */
    private final TypeReference<T> responseTypeReference;

    /**
     * Jackson JavaType 响应类型。
     */
    private final JavaType responseJavaType;

    /**
     * 自定义响应解析器，优先级高于默认 JSON 解析。
     */
    private final OutboundResponseParser<T> responseParser;

    public OutboundRequestInfo(OutboundRequestInfoBuilder<T> builder) {
        this.thirdIdentity = builder.getThirdIdentity();
        this.operation = builder.getOperation();
        this.url = builder.getUrl();
        this.method = builder.getMethod();
        this.headers = unmodifiableCopy(builder.getHeaders());
        this.queryParams = unmodifiableCopy(builder.getQueryParams());
        this.formParams = unmodifiableCopy(builder.getFormParams());
        this.requestType = builder.getRequestType();
        this.body = builder.getBody();
        this.timeout = builder.getTimeout();
        this.responseType = builder.getResponseType();
        this.responseTypeReference = builder.getResponseTypeReference();
        this.responseJavaType = builder.getResponseJavaType();
        this.responseParser = builder.getResponseParser();
    }

    public static <T> OutboundRequestInfoBuilder<T> builder(ThirdIdentity thirdIdentity) {
        return OutboundRequestInfoBuilder.create(thirdIdentity);
    }

    private Map<String, String> unmodifiableCopy(Map<String, String> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }
}
