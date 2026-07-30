package cn.xuqiudong.basic.third.outbound.api;

import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;

/**
 * 第三方出站 API 定义。
 *
 * <p>具体项目通常用枚举实现本接口，一个枚举值对应一个第三方接口。</p>
 *
 * @author Vic.xu
 */
public interface ThirdApi {

    /**
     * API 名称，用于日志和排查。
     */
    String getApiName();

    /**
     * API 路径或路径配置项。
     *
     * <p>如果项目路径固定，可直接返回 {@code /api/order}；如果路径来自配置，可返回配置项编码，
     * 再由具体 {@code AbstractOutboundPartner} 子类解释。</p>
     */
    String getPath();

    /**
     * HTTP method。
     */
    ThirdHttpMethod getMethod();

    /**
     * 成功码，具体厂商 Client 可按需使用。
     */
    default String getSuccessCode() {
        return null;
    }
}
