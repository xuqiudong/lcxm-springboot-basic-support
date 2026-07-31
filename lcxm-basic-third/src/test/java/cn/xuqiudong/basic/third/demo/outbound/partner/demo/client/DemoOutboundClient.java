package cn.xuqiudong.basic.third.demo.outbound.partner.demo.client;

import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.config.DemoOutboundConfig;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.enums.DemoApi;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoRequest;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoResponse;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoThirdResponse;
import cn.xuqiudong.basic.third.outbound.builder.OutboundRequestInfoBuilder;
import cn.xuqiudong.basic.third.outbound.client.AbstractOutboundPartner;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.util.ResponseTypeUtils;
import com.fasterxml.jackson.databind.JavaType;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 出站 demo 厂商 Client。
 *
 * <p>真实项目在这个子类里封装 token、签名、成功码判断，以及贴近业务语义的调用方法。</p>
 */
public class DemoOutboundClient extends AbstractOutboundPartner<DemoOutboundConfig, DemoApi> {

    public DemoOutboundClient(ThirdClientOptions options, OutboundExecutor executor) {
        super(options, executor);
    }

    @Override
    protected ThirdIdentity thirdIdentity() {
        return ThirdIdentity.of("demo-partner", "Demo 第三方");
    }

    @Override
    protected Map<String, String> buildHeaders(DemoApi api) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + getToken());
        return headers;
    }

    /**
     * 通用 JSON 请求封装：适合该厂商多数接口都是统一响应包裹的场景。
     */
    public <T, R> DemoThirdResponse<R> request(DemoApi api, T body, Class<R> responseType) {
        JavaType javaType = ResponseTypeUtils.objectType(DemoThirdResponse.class, responseType);
        return request(api, body, javaType);
    }

    /**
     * 通用 JSON 请求封装：支持复杂泛型响应。
     */
    public <T, R> DemoThirdResponse<R> request(DemoApi api, T body, JavaType javaType) {
        return super.requestJson(api, body, javaType);
    }

    /**
     * 简单 JSON POST：直接复用通用 JSON 终结方法。
     */
    public DemoThirdResponse<DemoResponse> submitOrder(DemoRequest request) {
        return request(DemoApi.SUBMIT_ORDER, request, DemoResponse.class);
    }

    /**
     * 参数较多但结构单一：先在子类中组织 Map，再调用 requestForm 终结方法。
     */
    public DemoThirdResponse<DemoResponse> queryOrderByForm(String orderNo, String tenantId) {
        Map<String, Object> params = createParams()
                .add("orderNo", orderNo)
                .addIfNotBlank("tenantId", tenantId)
                .toMap();
        OutboundRequestInfo<DemoThirdResponse<DemoResponse>> request =
                responseBuilder(DemoApi.QUERY_ORDER_FORM)
                        .formParams(params)
                        .build();
        return execute(DemoApi.QUERY_ORDER_FORM, request);
    }

    /**
     * 复杂请求：快捷终结方法不满足时，从 builder 开始追加 query/header 等参数，再 execute。
     */
    public DemoThirdResponse<DemoResponse> queryOrder(String orderNo) {
        OutboundRequestInfo<DemoThirdResponse<DemoResponse>> request =
                responseBuilder(DemoApi.QUERY_ORDER)
                        .queryParam("orderNo", orderNo)
                        .header("X-Demo-Trace", "query-order")
                        .build();
        return execute(DemoApi.QUERY_ORDER, request);
    }

    /**
     * 文件上传：单文件可直接用 requestMultipart 终结方法。
     */
    public DemoThirdResponse<DemoResponse> uploadOrderFile(File file) {
        OutboundRequestInfo<DemoThirdResponse<DemoResponse>> request =
                responseBuilder(DemoApi.UPLOAD_ORDER_FILE)
                        .multipartFile("file", file)
                        .build();
        return execute(DemoApi.UPLOAD_ORDER_FILE, request);
    }

    /**
     * 复杂文件上传：需要 query、普通 multipart 字段、文件同时存在时，从 builder 开始。
     */
    public DemoThirdResponse<DemoResponse> uploadOrderFile(String orderNo, File file) {
        OutboundRequestInfo<DemoThirdResponse<DemoResponse>> request =
                responseBuilder(DemoApi.UPLOAD_ORDER_FILE)
                        .queryParam("orderNo", orderNo)
                        .multipartParam("bizType", "order")
                        .multipartFile("file", file)
                        .build();
        return execute(DemoApi.UPLOAD_ORDER_FILE, request);
    }

    @Override
    protected <R> void afterResponse(DemoApi api, R response) {
        if (response instanceof DemoThirdResponse<?> thirdResponse
                && !thirdResponse.success(api.getSuccessCode())) {
            throw new ThirdException("demo third api failed: " + api.getApiName() + ", " + thirdResponse.getMessage());
        }
    }

    private String getToken() {
        DemoOutboundConfig config = getConfig();
        return config.getClientId() + ":" + config.getClientSecret();
    }

    private JavaType responseJavaType() {
        return ResponseTypeUtils.objectType(DemoThirdResponse.class, DemoResponse.class);
    }

    /**
     * demo 厂商统一响应构建器，隐藏 JavaType 泛型细节。
     */
    private OutboundRequestInfoBuilder<DemoThirdResponse<DemoResponse>> responseBuilder(DemoApi api) {
        return this.<DemoThirdResponse<DemoResponse>>builder(api, responseJavaType());
    }

    /**
     * demo 配置读取逻辑。
     *
     * <p>真实项目可在这里读取数据库、Redis、配置中心，或调用项目自己的配置服务。
     * 基类默认会对加载结果做 Caffeine 短缓存。</p>
     */
    @Override
    protected DemoOutboundConfig loadConfig() {
        DemoOutboundConfig config = new DemoOutboundConfig();
        config.setHost("https://third.example.com");
        config.setClientId("demo-client-id");
        config.setClientSecret("demo-client-secret");
        config.setClientOptions(new ThirdClientOptions()
                .addDefaultHeader("X-Demo-Client", "demo"));
        return config;
    }
}
