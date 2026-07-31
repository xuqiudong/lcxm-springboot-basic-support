package cn.xuqiudong.basic.third.outbound.executor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.log.model.ThirdExchangeLog;
import cn.xuqiudong.basic.third.log.model.ThirdExchangeStatus;
import cn.xuqiudong.basic.third.log.service.Slf4jThirdExchangeLogger;
import cn.xuqiudong.basic.third.log.service.ThirdExchangeLogger;
import cn.xuqiudong.basic.third.outbound.model.MultipartPart;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestType;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Hutool HTTP implementation of {@link OutboundExecutor}.
 *
 * @author Vic.xu
 */
public class HttpOutboundExecutor implements OutboundExecutor {

    private final ObjectMapper objectMapper;

    private final ThirdClientOptions options;

    private final ThirdExchangeLogger exchangeLogger;

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String ACCEPT = "Accept";

    public HttpOutboundExecutor() {
        this(new ThirdClientOptions());
    }

    public HttpOutboundExecutor(ThirdClientOptions options) {
        this(options, defaultObjectMapper(), null);
    }

    public HttpOutboundExecutor(ThirdClientOptions options, ObjectMapper objectMapper,
            ThirdExchangeLogger exchangeLogger) {
        this.options = options == null ? new ThirdClientOptions() : options;
        this.objectMapper = objectMapper == null ? defaultObjectMapper() : objectMapper;
        this.exchangeLogger = exchangeLogger == null ? new Slf4jThirdExchangeLogger() : exchangeLogger;
    }

    @Override
    public <T> T execute(OutboundRequestInfo<T> request) {
        validateRequest(request);
        long start = DateUtil.current();
        Integer httpStatus = null;
        String requestBody = null;
        String responseBody = null;
        try {
            HttpRequest httpRequest = buildRequest(request);
            requestBody = resolveLogRequestBody(request);
            try (HttpResponse response = httpRequest.execute()) {
                httpStatus = response.getStatus();
                responseBody = response.body();
            }
            checkHttpStatus(httpStatus);
            T result = parse(responseBody, request);
            log(request, httpStatus, DateUtil.current() - start, requestBody, responseBody, null);
            return result;
        } catch (Exception e) {
            log(request, httpStatus, DateUtil.current() - start, requestBody, responseBody, e);
            if (e instanceof ThirdException) {
                throw (ThirdException) e;
            }
            throw new ThirdException("execute outbound request failed", e);
        }
    }

    @Override
    public byte[] executeBytes(OutboundRequestInfo<?> request) {
        validateRequest(request);
        long start = DateUtil.current();
        Integer httpStatus = null;
        String requestBody = null;
        try {
            HttpRequest httpRequest = buildRequest(request);
            requestBody = resolveLogRequestBody(request);
            byte[] bodyBytes;
            try (HttpResponse response = httpRequest.execute()) {
                httpStatus = response.getStatus();
                bodyBytes = response.bodyBytes();
            }
            checkHttpStatus(httpStatus);
            log(request, httpStatus, DateUtil.current() - start, requestBody, null, null);
            return bodyBytes;
        } catch (Exception e) {
            log(request, httpStatus, DateUtil.current() - start, requestBody, null, e);
            if (e instanceof ThirdException) {
                throw (ThirdException) e;
            }
            throw new ThirdException("execute outbound bytes request failed", e);
        }
    }

    private void validateRequest(OutboundRequestInfo<?> request) {
        if (request == null) {
            throw new ThirdException("outbound request can not be null");
        }
    }

    private HttpRequest buildRequest(OutboundRequestInfo<?> request) throws Exception {
        HttpRequest httpRequest = HttpRequest.of(appendQuery(request.getUrl(), request.getQueryParams()))
                .method(toHutoolMethod(request.getMethod()))
                .charset(StandardCharsets.UTF_8)
                .timeout(toMillis(resolveTimeout(request)));
        if (options.getProxy() != null) {
            httpRequest.setProxy(options.getProxy());
        }
        httpRequest.addHeaders(options.getDefaultHeaders());
        httpRequest.addHeaders(request.getHeaders());
        applyRequestTypeHeaders(httpRequest, request);
        writeRequestBody(httpRequest, request);
        return httpRequest;
    }

    private String appendQuery(String url, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return url;
        }
        StringJoiner joiner = new StringJoiner("&");
        queryParams.forEach((key, value) ->
                joiner.add(URLUtil.encodeQuery(key) + "=" + URLUtil.encodeQuery(value == null ? "" : value)));
        return url.contains("?") ? url + "&" + joiner : url + "?" + joiner;
    }

    private Map<String, Object> toObjectMap(Map<String, String> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach(result::put);
        return result;
    }

    private Duration resolveTimeout(OutboundRequestInfo<?> request) {
        return request.getTimeout() == null ? options.getRequestTimeout() : request.getTimeout();
    }

    private int toMillis(Duration duration) {
        return duration == null ? 30000 : Math.toIntExact(duration.toMillis());
    }

    private Method toHutoolMethod(ThirdHttpMethod method) {
        return Method.valueOf((method == null ? ThirdHttpMethod.GET : method).name());
    }

    private void applyRequestTypeHeaders(HttpRequest httpRequest, OutboundRequestInfo<?> request) {
        OutboundRequestType requestType = resolveRequestType(request);
        String contentType = requestType.getContentType();
        if (contentType != null && !hasHeader(request, CONTENT_TYPE)) {
            httpRequest.header(CONTENT_TYPE, contentType);
        }
        if (OutboundRequestType.JSON == requestType && !hasHeader(request, ACCEPT)) {
            httpRequest.header(ACCEPT, OutboundRequestType.JSON.getContentType());
        }
    }

    private boolean hasHeader(OutboundRequestInfo<?> request, String name) {
        return containsHeader(options.getDefaultHeaders(), name) || containsHeader(request.getHeaders(), name);
    }

    private boolean containsHeader(Map<String, String> headers, String name) {
        if (headers == null || headers.isEmpty()) {
            return false;
        }
        return headers.keySet().stream().anyMatch(key -> key.equalsIgnoreCase(name));
    }

    private void writeRequestBody(HttpRequest httpRequest, OutboundRequestInfo<?> request) throws Exception {
        OutboundRequestType requestType = resolveRequestType(request);
        switch (requestType) {
            case FORM:
                httpRequest.form(toObjectMap(request.getFormParams()));
                break;
            case MULTIPART:
                writeMultipartBody(httpRequest, request.getMultipartParts());
                break;
            case TEXT:
                if (request.getBody() != null) {
                    httpRequest.body(String.valueOf(request.getBody()));
                }
                break;
            case BYTES:
                if (request.getBody() instanceof byte[]) {
                    httpRequest.body((byte[]) request.getBody());
                } else if (request.getBody() != null) {
                    httpRequest.body(String.valueOf(request.getBody()).getBytes(StandardCharsets.UTF_8));
                }
                break;
            case JSON:
                if (request.getBody() != null) {
                    httpRequest.body(resolveJsonBody(request.getBody()));
                }
                break;
            case QUERY:
            case NONE:
            default:
                break;
        }
    }

    private OutboundRequestType resolveRequestType(OutboundRequestInfo<?> request) {
        return request.getRequestType() == null ? OutboundRequestType.NONE : request.getRequestType();
    }

    private String resolveLogRequestBody(OutboundRequestInfo<?> request) throws Exception {
        OutboundRequestType requestType = resolveRequestType(request);
        if (OutboundRequestType.FORM == requestType) {
            return request.getFormParams().isEmpty() ? null : request.getFormParams().toString();
        }
        if (OutboundRequestType.MULTIPART == requestType) {
            return request.getMultipartParts().isEmpty() ? null : summarizeMultipartParts(request.getMultipartParts());
        }
        if (OutboundRequestType.BYTES == requestType) {
            return request.getBody() instanceof byte[] ? "byte[" + ((byte[]) request.getBody()).length + "]" : null;
        }
        if (request.getBody() == null) {
            return null;
        }
        return OutboundRequestType.JSON == requestType ? resolveJsonBody(request.getBody()) : String.valueOf(request.getBody());
    }

    private String resolveJsonBody(Object body) throws Exception {
        if (body == null) {
            return null;
        }
        return body instanceof String ? (String) body : objectMapper.writeValueAsString(body);
    }

    private void writeMultipartBody(HttpRequest httpRequest, List<MultipartPart> multipartParts) {
        if (multipartParts == null || multipartParts.isEmpty()) {
            return;
        }
        for (MultipartPart part : multipartParts) {
            switch (part.getType()) {
                case FIELD:
                    httpRequest.form(part.getName(), part.getValue());
                    break;
                case FILE:
                    if (StrUtil.isBlank(part.getFileName())) {
                        httpRequest.form(part.getName(), part.getFile());
                    } else {
                        httpRequest.form(part.getName(), part.getFile(), part.getFileName());
                    }
                    break;
                case STREAM:
                    httpRequest.form(part.getName(), new InputStreamResource(part.getInputStream(), part.getFileName()));
                    break;
                default:
                    break;
            }
        }
    }

    private String summarizeMultipartParts(List<MultipartPart> multipartParts) {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (MultipartPart part : multipartParts) {
            joiner.add(part.getName() + "=" + summarizeMultipartPart(part));
        }
        return joiner.toString();
    }

    private String summarizeMultipartPart(MultipartPart part) {
        switch (part.getType()) {
            case FIELD:
                return "String";
            case FILE:
                return "File(" + part.getFile().getName() + ")";
            case STREAM:
                return "InputStream(" + part.getFileName() + ")";
            default:
                return part.getType().name();
        }
    }

    private <T> T parse(String responseBody, OutboundRequestInfo<T> request) throws Exception {
        if (request.getResponseParser() != null) {
            return request.getResponseParser().parse(responseBody);
        }
        if (StrUtil.isBlank(responseBody)) {
            return null;
        }
        if (request.getResponseTypeReference() != null) {
            return objectMapper.readValue(responseBody, request.getResponseTypeReference());
        }
        if (request.getResponseJavaType() != null) {
            return objectMapper.readValue(responseBody, request.getResponseJavaType());
        }
        Class<T> responseType = request.getResponseType();
        if (responseType == null) {
            throw new ThirdException("No response parser defined");
        }
        if (String.class.equals(responseType)) {
            return responseType.cast(responseBody);
        }
        return objectMapper.readValue(responseBody, responseType);
    }

    private void checkHttpStatus(Integer httpStatus) {
        if (httpStatus != null && httpStatus >= 400) {
            throw new ThirdException("third http status failed: " + httpStatus);
        }
    }

    private void log(OutboundRequestInfo<?> request, Integer httpStatus, long elapsedMillis, String requestBody,
            String responseBody, Exception exception) {
        if (!options.isExchangeLogEnabled()) {
            return;
        }
        ThirdExchangeLog log = new ThirdExchangeLog()
                .setThirdCode(request.getThirdIdentity().getCode())
                .setThirdName(request.getThirdIdentity().getName())
                .setOperation(request.getOperation())
                .setMethod(request.getMethod().name())
                .setUrl(request.getUrl())
                .setHttpStatus(httpStatus)
                .setElapsedMillis(elapsedMillis)
                .setRequestBody(requestBody)
                .setResponseBody(responseBody)
                .setStatus(resolveStatus(httpStatus, exception))
                .setErrorMessage(exception == null ? null : exception.getMessage());
        exchangeLogger.log(log);
    }

    /**
     * HTTP 错误状态码或本地异常都视为失败交换。
     */
    private ThirdExchangeStatus resolveStatus(Integer httpStatus, Exception exception) {
        if (exception != null || (httpStatus != null && httpStatus >= 400)) {
            return ThirdExchangeStatus.FAILED;
        }
        return ThirdExchangeStatus.SUCCESS;
    }

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
