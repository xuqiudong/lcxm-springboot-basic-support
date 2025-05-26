package cn.xuqiudong.common.base.srpc.model;

import java.io.Serializable;

/**
 * 描述: 携带PRC调用的核心数据：url，request，interfaceName
 * @author Vic.xu
 * @since 2024-06-25
 */
public class Invoker implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 包含请求地址
     */
    private XqdUrl xqdUrl;

    /**
     * 请求参数
     */
    private XqdRequest xqdRequest;

    /**
     * 当前请求对应的接口全限定名称
     */
    private String interfaceName;

    /**
     * 请求标识
     */
    private long requestId;

    /**
     * 接口方法的返回类型
     */
    private Class<?> returnType;

    public XqdUrl getXqdUrl() {
        return xqdUrl;
    }

    public void setXqdUrl(XqdUrl xqdUrl) {
        this.xqdUrl = xqdUrl;
    }

    public XqdRequest getXqdRequest() {
        return xqdRequest;
    }

    public void setXqdRequest(XqdRequest xqdRequest) {
        this.xqdRequest = xqdRequest;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }
}
