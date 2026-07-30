package cn.xuqiudong.basic.third.demo.outbound.partner.demo.enums;

import cn.xuqiudong.basic.third.outbound.api.ThirdApi;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;

/**
 * 出站 demo API 枚举。
 *
 * <p>真实项目一个第三方通常定义一个 API 枚举。</p>
 */
public enum DemoApi implements ThirdApi {

    GET_TOKEN("获取 Token", "/token", ThirdHttpMethod.POST, "0"),

    SUBMIT_ORDER("提交订单", "/order/submit", ThirdHttpMethod.POST, "0000"),

    QUERY_ORDER("查询订单", "/order/query", ThirdHttpMethod.GET, "0000");

    private final String apiName;

    private final String path;

    private final ThirdHttpMethod method;

    private final String successCode;

    DemoApi(String apiName, String path, ThirdHttpMethod method, String successCode) {
        this.apiName = apiName;
        this.path = path;
        this.method = method;
        this.successCode = successCode;
    }

    @Override
    public String getApiName() {
        return apiName;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public ThirdHttpMethod getMethod() {
        return method;
    }

    @Override
    public String getSuccessCode() {
        return successCode;
    }
}
