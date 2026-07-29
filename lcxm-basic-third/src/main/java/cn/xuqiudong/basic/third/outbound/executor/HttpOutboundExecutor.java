package cn.xuqiudong.basic.third.outbound.executor;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import cn.hutool.core.date.DateUtil;
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
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Hutool HTTP implementation of {@link OutboundExecutor}.
 *
 * @author Vic.xu
 */
public class HttpOutboundExecutor implements OutboundExecutor {

    private final ObjectMapper objectMapper;

    private final ThirdClientOptions options;

    private final ThirdExchangeLogger exchangeLogger;

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
        long start = DateUtil.current();
        Integer httpStatus = null;
        String requestBody = null;
        String responseBody = null;
        try {
            HttpRequest httpRequest = buildRequest(request);
            requestBody = resolveRequestBody(request);
            try (HttpResponse response = httpRequest.execute()) {
                httpStatus = response.getStatus();
                responseBody = response.body();
            }
            T result = parse(responseBody, request);
            log(request, httpStatus, DateUtil.current() - start, requestBody, responseBody, null);
            return result;
        } catch (Exception e) {
            log(request, httpStatus, DateUtil.current() - start, requestBody, responseBody, e);
            if (e instanceof ThirdException thirdException) {
                throw thirdException;
            }
            throw new ThirdException("execute outbound request failed", e);
        }
    }

    @Override
    public byte[] executeBytes(OutboundRequestInfo<?> request) {
        long start = DateUtil.current();
        Integer httpStatus = null;
        String requestBody = null;
        try {
            HttpRequest httpRequest = buildRequest(request);
            requestBody = resolveRequestBody(request);
            byte[] bodyBytes;
            try (HttpResponse response = httpRequest.execute()) {
                httpStatus = response.getStatus();
                bodyBytes = response.bodyBytes();
            }
            log(request, httpStatus, DateUtil.current() - start, requestBody, null, null);
            return bodyBytes;
        } catch (Exception e) {
            log(request, httpStatus, DateUtil.current() - start, requestBody, null, e);
            throw new ThirdException("execute outbound bytes request failed", e);
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
        if (request.getBody() != null) {
            httpRequest.body(resolveRequestBody(request));
        } else if (!request.getFormParams().isEmpty()) {
            httpRequest.form(toObjectMap(request.getFormParams()));
        }
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

    private String resolveRequestBody(OutboundRequestInfo<?> request) throws Exception {
        if (request.getBody() == null) {
            return null;
        }
        return request.getBody() instanceof String
                ? (String) request.getBody()
                : objectMapper.writeValueAsString(request.getBody());
    }

    private <T> T parse(String responseBody, OutboundRequestInfo<T> request) throws Exception {
        if (request.getResponseParser() != null) {
            return request.getResponseParser().parse(responseBody);
        }
        if (responseBody == null || responseBody.isBlank()) {
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
