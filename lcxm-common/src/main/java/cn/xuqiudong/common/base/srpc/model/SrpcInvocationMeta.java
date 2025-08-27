package cn.xuqiudong.common.base.srpc.model;

import cn.xuqiudong.common.base.srpc.annotation.SrpcMethod;
import cn.xuqiudong.common.base.srpc.annotation.SrpcReference;
import org.apache.commons.lang3.StringUtils;

/**
 * 描述:
 * 某个接口 每个方法 需要额外指定的元信息, 其属性 伴随 SrpcMethod 增减
 *
 * @author Vic.xu
 * @see cn.xuqiudong.common.base.srpc.annotation.SrpcReference
 * @see cn.xuqiudong.common.base.srpc.annotation.SrpcMethod
 * @since 2025-08-27 9:31
 */
public class SrpcInvocationMeta {

    /**
     * 区分调用不同的服务
     */
    String serviceCode;

    /**
     * 超时时间
     */
    int timeout;

    /**
     * 构造方法
     *
     * @param referenceAnnotation SrpcReference
     */
    public SrpcInvocationMeta(SrpcReference referenceAnnotation) {
        this.serviceCode = referenceAnnotation.serviceCode();
        this.timeout = referenceAnnotation.timeout();
    }


    /**
     * 根据方法获取元信息(如果方SrpcMethod上配置的为默认 则从SrpcReference 获取元信息)
     *
     * @param method              SrpcMethod
     * @param referenceAnnotation SrpcReference
     */
    public SrpcInvocationMeta(SrpcMethod method, SrpcReference referenceAnnotation) {
        this.serviceCode = method.serviceCode();
        if (StringUtils.isBlank(this.serviceCode)) {
            this.serviceCode = referenceAnnotation.serviceCode();
        }
        this.timeout = method.timeout();
        if (this.timeout <= 0) {
            this.timeout = referenceAnnotation.timeout();
        }
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
