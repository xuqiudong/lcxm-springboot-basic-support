package cn.xuqiudong.basic.core.srpc.model;

import java.io.Serializable;

/**
 * 描述:rpc请求参数
 * @author Vic.xu
 * @date 2022-02-22 9:12
 */
public class XqdRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    private String interfaceName;

    private String methodName;

    private Class[] paramTypes;

    private Object[] params;

    public XqdRequest(String interfaceName, String methodName, Class[] paramTypes, Object[] params) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.params = params;
    }

    public XqdRequest() {
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
