package cn.xuqiudong.basic.third.outbound.builder;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.outbound.model.MultipartPart;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestType;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import cn.xuqiudong.basic.third.outbound.parser.OutboundResponseParser;
import cn.xuqiudong.basic.third.outbound.util.ResponseTypeUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;

/**
 * 出站请求构建器。
 *
 * <p>业务 Client 用它描述一次 HTTP 请求，再交给 {@code OutboundExecutor} 执行。</p>
 *
 * @author Vic.xu
 */
@Getter
public class OutboundRequestInfoBuilder<T> {

    private final ThirdIdentity thirdIdentity;

    private String operation;

    private String url;

    private ThirdHttpMethod method = ThirdHttpMethod.GET;

    private final Map<String, String> headers = new LinkedHashMap<>();

    private final Map<String, String> queryParams = new LinkedHashMap<>();

    private final Map<String, String> formParams = new LinkedHashMap<>();

    private final List<MultipartPart> multipartParts = new ArrayList<>();

    private OutboundRequestType requestType;

    private Object body;

    private Duration timeout;

    private Class<T> responseType;

    private TypeReference<T> responseTypeReference;

    private JavaType responseJavaType;

    private OutboundResponseParser<T> responseParser;

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for invalid builder input.")
    private OutboundRequestInfoBuilder(ThirdIdentity thirdIdentity) {
        if (thirdIdentity == null) {
            throw new IllegalArgumentException("thirdIdentity can not be null");
        }
        this.thirdIdentity = thirdIdentity;
    }

    /**
     * 创建请求构建器。
     */
    public static <T> OutboundRequestInfoBuilder<T> create(ThirdIdentity thirdIdentity) {
        return new OutboundRequestInfoBuilder<>(thirdIdentity);
    }

    /**
     * 设置业务操作名，用于日志和排查。
     */
    public OutboundRequestInfoBuilder<T> operation(String operation) {
        this.operation = operation;
        return this;
    }

    /**
     * 设置完整请求 URL。
     */
    public OutboundRequestInfoBuilder<T> url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置 HTTP method。
     */
    public OutboundRequestInfoBuilder<T> method(ThirdHttpMethod method) {
        if (method != null) {
            this.method = method;
        }
        return this;
    }

    /**
     * 批量设置 header。
     */
    public OutboundRequestInfoBuilder<T> headers(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
        return this;
    }

    /**
     * 设置单个 header。
     */
    public OutboundRequestInfoBuilder<T> header(String name, String value) {
        if (StrUtil.isNotBlank(name) && value != null) {
            this.headers.put(name, value);
        }
        return this;
    }

    /**
     * 设置单个 query 参数。
     */
    public OutboundRequestInfoBuilder<T> queryParam(String name, Object value) {
        if (StrUtil.isNotBlank(name) && value != null) {
            this.queryParams.put(name, String.valueOf(value));
        }
        return this;
    }

    /**
     * 批量设置 query 参数。
     */
    public OutboundRequestInfoBuilder<T> queryParams(Map<String, ?> queryParams) {
        if (queryParams != null) {
            queryParams.forEach(this::queryParam);
        }
        return this;
    }

    /**
     * 设置单个 form 参数。
     */
    public OutboundRequestInfoBuilder<T> formParam(String name, Object value) {
        if (StrUtil.isNotBlank(name) && value != null) {
            this.formParams.put(name, String.valueOf(value));
            this.requestType = OutboundRequestType.FORM;
        }
        return this;
    }

    /**
     * 批量设置 form 参数。
     */
    public OutboundRequestInfoBuilder<T> formParams(Map<String, ?> formParams) {
        if (formParams != null) {
            formParams.forEach(this::formParam);
        }
        return this;
    }

    /**
     * 设置 multipart 普通字段。
     */
    public OutboundRequestInfoBuilder<T> multipartParam(String name, String value) {
        if (StrUtil.isNotBlank(name) && value != null) {
            this.multipartParts.add(MultipartPart.field(name, value));
            this.requestType = OutboundRequestType.MULTIPART;
        }
        return this;
    }

    /**
     * 批量设置 multipart 字段。
     */
    public OutboundRequestInfoBuilder<T> multipartParams(Map<String, String> multipartParams) {
        if (multipartParams != null) {
            multipartParams.forEach(this::multipartParam);
        }
        return this;
    }

    /**
     * 设置 multipart 文件。
     */
    public OutboundRequestInfoBuilder<T> multipartFile(String name, File file) {
        if (StrUtil.isNotBlank(name) && file != null) {
            this.multipartParts.add(MultipartPart.file(name, file));
            this.requestType = OutboundRequestType.MULTIPART;
        }
        return this;
    }

    /**
     * 设置 multipart 文件，并显式指定上传文件名。
     */
    public OutboundRequestInfoBuilder<T> multipartFile(String name, File file, String fileName) {
        if (StrUtil.isNotBlank(name) && file != null) {
            this.multipartParts.add(MultipartPart.file(name, file, fileName));
            this.requestType = OutboundRequestType.MULTIPART;
        }
        return this;
    }

    /**
     * 设置同一个 field name 下的多个 File 文件。
     */
    public OutboundRequestInfoBuilder<T> multipartFiles(String name, Collection<File> files) {
        if (files != null) {
            files.forEach(file -> multipartFile(name, file));
        }
        return this;
    }

    /**
     * 设置同一个 field name 下的多个 File 文件。
     */
    public OutboundRequestInfoBuilder<T> multipartFiles(String name, File... files) {
        if (files != null) {
            for (File file : files) {
                multipartFile(name, file);
            }
        }
        return this;
    }

    /**
     * 设置 multipart 文件流。
     *
     * <p>正常执行到 multipart 写入时，Hutool 会在读取后关闭流；如果请求在写入前失败，调用方仍应自行兜底关闭。</p>
     */
    public OutboundRequestInfoBuilder<T> multipartFile(String name, String fileName, InputStream inputStream) {
        if (StrUtil.isNotBlank(name) && inputStream != null && StrUtil.isNotBlank(fileName)) {
            this.multipartParts.add(MultipartPart.stream(name, fileName, inputStream));
            this.requestType = OutboundRequestType.MULTIPART;
        }
        return this;
    }

    /**
     * 设置 JSON 请求体；对象会由默认执行器序列化为 JSON。
     */
    public OutboundRequestInfoBuilder<T> body(Object body) {
        return jsonBody(body);
    }

    /**
     * 设置 JSON 请求体。
     */
    public OutboundRequestInfoBuilder<T> jsonBody(Object body) {
        this.body = body;
        this.requestType = body == null ? this.requestType : OutboundRequestType.JSON;
        return this;
    }

    /**
     * 设置纯文本请求体。
     */
    public OutboundRequestInfoBuilder<T> textBody(String body) {
        this.body = body;
        this.requestType = body == null ? this.requestType : OutboundRequestType.TEXT;
        return this;
    }

    /**
     * 设置二进制请求体。
     */
    public OutboundRequestInfoBuilder<T> bytesBody(byte[] body) {
        this.body = body;
        this.requestType = body == null ? this.requestType : OutboundRequestType.BYTES;
        return this;
    }

    /**
     * 显式设置请求体类型；通常优先使用 jsonBody/formParams/textBody/bytesBody。
     */
    public OutboundRequestInfoBuilder<T> requestType(OutboundRequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    /**
     * 设置本次请求超时时间；为空时使用第三方默认配置。
     */
    public OutboundRequestInfoBuilder<T> timeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 设置响应 Class 类型。
     */
    public OutboundRequestInfoBuilder<T> responseType(Class<T> responseType) {
        this.responseType = responseType;
        return this;
    }

    /**
     * 设置泛型响应类型。
     */
    public OutboundRequestInfoBuilder<T> responseType(TypeReference<T> responseTypeReference) {
        this.responseTypeReference = responseTypeReference;
        return this;
    }

    /**
     * 设置 Jackson JavaType 响应类型。
     */
    public OutboundRequestInfoBuilder<T> responseJavaType(JavaType responseJavaType) {
        this.responseJavaType = responseJavaType;
        return this;
    }

    /**
     * 设置包装对象响应类型，例如 {@code ThirdResponse<OrderDTO>}。
     */
    public OutboundRequestInfoBuilder<T> wrapperResponseType(Class<?> wrapper, Class<?> inner) {
        this.responseJavaType = ResponseTypeUtils.objectType(wrapper, inner);
        return this;
    }

    /**
     * 设置包装 List 响应类型，例如 {@code ThirdResponse<List<OrderDTO>>}。
     */
    public OutboundRequestInfoBuilder<T> wrapperListResponseType(Class<?> wrapper, Class<?> element) {
        this.responseJavaType = ResponseTypeUtils.wrapperListType(wrapper, element);
        return this;
    }

    /**
     * 设置自定义响应解析器，适合非标准 JSON、纯文本等响应。
     */
    public OutboundRequestInfoBuilder<T> responseParser(OutboundResponseParser<T> responseParser) {
        this.responseParser = responseParser;
        return this;
    }

    /**
     * 快捷设置 GET 请求。
     */
    public OutboundRequestInfoBuilder<T> get(String url, Class<T> responseType) {
        return url(url).method(ThirdHttpMethod.GET).responseType(responseType);
    }

    /**
     * 快捷设置 POST 请求。
     */
    public OutboundRequestInfoBuilder<T> post(String url, Object body, Class<T> responseType) {
        return url(url).method(ThirdHttpMethod.POST).jsonBody(body).responseType(responseType);
    }

    /**
     * 构建不可变请求模型。
     */
    public OutboundRequestInfo<T> build() {
        if (StrUtil.isBlank(url)) {
            throw new IllegalArgumentException("url can not be blank");
        }
        this.requestType = inferRequestType();
        return new OutboundRequestInfo<>(this);
    }

    private OutboundRequestType inferRequestType() {
        if (requestType != null) {
            return requestType;
        }
        if (body != null) {
            return OutboundRequestType.JSON;
        }
        if (!multipartParts.isEmpty()) {
            return OutboundRequestType.MULTIPART;
        }
        if (!formParams.isEmpty()) {
            return OutboundRequestType.FORM;
        }
        if (!queryParams.isEmpty()) {
            return OutboundRequestType.QUERY;
        }
        return OutboundRequestType.NONE;
    }
}
