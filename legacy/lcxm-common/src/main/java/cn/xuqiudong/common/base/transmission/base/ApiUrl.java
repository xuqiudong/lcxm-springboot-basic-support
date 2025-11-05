package cn.xuqiudong.common.base.transmission.base;

import org.springframework.http.HttpMethod;

/**
 * 描述: 请求地址接口，建议子类使用枚举
 * @author Vic.xu
 * @since 2022-08-17 11:32
 */
public interface ApiUrl {


    /**
     * 请求方式
     * @return GET POST etc.
     */
    HttpMethod getMethod();

    /**
     * 请求地址
     * @return 完整的url或者局部url然后统一加前缀
     */
    String getUrl();

    /**
     * 请求说明：一般表示接口名
     * @return 接口说明
     */
    String getText();

}
