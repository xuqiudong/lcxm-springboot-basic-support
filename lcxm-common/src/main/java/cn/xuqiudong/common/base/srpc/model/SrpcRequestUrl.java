package cn.xuqiudong.common.base.srpc.model;

import cn.xuqiudong.common.base.srpc.constant.SimpleRpcConstant;
import org.apache.http.client.methods.HttpPost;

import java.util.function.Consumer;

/**
 * 描述:
 *   简单rpc的请求信息
 * @author Vic.xu
 * @since 2024-07-01 10:42
 */
public class SrpcRequestUrl {

    /**
     * 主机地址
     */
    private String  host;

    /**
     * 设置通用请求session等信息
     * return httpPost -> {
     *       httpPost.setHeader("Content-Type", "application/json");
     *       httpPost.setHeader("Accept", "application/json");
     *     };
     */
    private Consumer<HttpPost> sessionInfo;

    public String getUrl(){
        return getHost() + SimpleRpcConstant.SIMPLE_RPC_URL;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Consumer<HttpPost> getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(Consumer<HttpPost> sessionInfo) {
        this.sessionInfo = sessionInfo;
    }
}
