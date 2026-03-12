package cn.xuqiudong.basic.third.transmission;

import cn.xuqiudong.basic.third.transmission.base.ApiUrl;

import java.net.Proxy;
import java.util.Map;

/**
 * 描述:
 * @author Vic.xu
 * @since 2022-08-19 10:07
 */
public class RequestBuilderFactory {

    CommonRequestInfo commonRequestInfo;


    /**
     * 通用的请求参数
     */
    public static class CommonRequestInfo {
        //超时时间
        int timeout;
        //通用请求前缀
        String baseUrl;
        // 通用请求头
        Map<String, String> commonHeader;
        //是否进行代理
        boolean isProxy;
        //代理
        private Proxy proxy;
        //第三方标识
        String thirdType;


    }


    public static class RealRequest {
        ApiUrl apiUrl;

        boolean requestJson;


    }

}
