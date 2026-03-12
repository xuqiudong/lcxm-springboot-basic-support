package cn.xuqiudong.basic.srpc.model;

import cn.xuqiudong.basic.srpc.constant.SimpleRpcConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 描述:
 * 简单rpc的请求信息
 *
 * @author Vic.xu
 * @since 2024-07-01 10:42
 */
public class SrpcRequestUrl {

    /**
     * 主机地址: 默认主机地址
     */
    private String host;

    /**
     * 主机地址: 默认主机地址
     */
    private Supplier<String> hostSupplier;

    /**
     * 指定某个应用对应的 主机地址
     */
    private Function<String, String> hostFunction;
    /**
     * 设置通用请求session等信息
     * return httpPost -> {
     * httpPost.setHeader("Content-Type", "application/json");
     * httpPost.setHeader("Accept", "application/json");
     * };
     */
    private Consumer<HttpPost> sessionInfo;

    public String getUrl() {
        if (hostSupplier != null) {
            return hostSupplier.get() + SimpleRpcConstant.SIMPLE_RPC_URL;
        }
        return getHost() + SimpleRpcConstant.SIMPLE_RPC_URL;
    }

    /**
     * 获取指定应用的请求地址
     *
     * @param serviceCode 应用编码
     */
    public String getUrl(String serviceCode) {
        if (StringUtils.isNotBlank(serviceCode) && hostFunction != null) {
            String url = hostFunction.apply(serviceCode) + SimpleRpcConstant.SIMPLE_RPC_URL;
            if (StringUtils.isNotBlank(url)) {
                return url;
            }
        }
        return getUrl();
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

    public Supplier<String> getHostSupplier() {
        return hostSupplier;
    }

    public void setHostSupplier(Supplier<String> hostSupplier) {
        this.hostSupplier = hostSupplier;
    }

    public Function<String, String> getHostFunction() {
        return hostFunction;
    }

    public void setHostFunction(Function<String, String> hostFunction) {
        this.hostFunction = hostFunction;
    }
}
