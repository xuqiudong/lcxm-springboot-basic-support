package cn.xuqiudong.basic.third.demo.outbound.partner.demo.client;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.config.DemoOutboundConfig;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.enums.DemoApi;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoRequest;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoResponse;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoThirdResponse;
import cn.xuqiudong.basic.third.outbound.api.ThirdApi;
import cn.xuqiudong.basic.third.outbound.client.AbstractOutboundPartner;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.util.ResponseTypeUtils;
import com.fasterxml.jackson.databind.JavaType;

/**
 * 出站 demo 厂商 Client。
 *
 * <p>真实项目在这里封装 token、签名、成功码判断和贴近业务的方法。</p>
 */
public class DemoOutboundClient extends AbstractOutboundPartner<DemoOutboundConfig> {

    public DemoOutboundClient(ThirdClientOptions options, OutboundExecutor executor) {
        super(options, executor);
    }

    @Override
    protected ThirdIdentity thirdIdentity() {
        return ThirdIdentity.of("demo-partner", "Demo 第三方");
    }

    @Override
    protected Map<String, String> buildHeaders(ThirdApi api) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + getToken());
        return headers;
    }

    /**
     * 请求第三方，返回第三方统一响应包装。
     */
    public <T, R> DemoThirdResponse<R> request(DemoApi api, T body, Class<R> responseType) {
        JavaType javaType = ResponseTypeUtils.objectType(DemoThirdResponse.class, responseType);
        return request(api, body, javaType);
    }

    /**
     * 请求第三方，支持复杂泛型响应。
     */
    public <T, R> DemoThirdResponse<R> request(DemoApi api, T body, JavaType javaType) {
        return super.request(api, body, javaType);
    }

    /**
     * 具体业务方法示例。
     */
    public DemoThirdResponse<DemoResponse> submitOrder(DemoRequest request) {
        return request(DemoApi.SUBMIT_ORDER, request, DemoResponse.class);
    }

    @Override
    protected <R> void afterResponse(ThirdApi api, R response) {
        if (response instanceof DemoThirdResponse<?> thirdResponse
                && !thirdResponse.success(api.getSuccessCode())) {
            throw new ThirdException("demo third api failed: " + api.getApiName() + ", " + thirdResponse.getMessage());
        }
    }

    private String getToken() {
        DemoOutboundConfig config = getConfig();
        return config.getClientId() + ":" + config.getClientSecret();
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
