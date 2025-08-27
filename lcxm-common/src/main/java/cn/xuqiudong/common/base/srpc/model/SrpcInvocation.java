package cn.xuqiudong.common.base.srpc.model;

import cn.xuqiudong.common.base.srpc.annotation.SrpcMethod;
import cn.xuqiudong.common.base.srpc.annotation.SrpcReference;
import cn.xuqiudong.common.base.srpc.proxy.jdk.JdkProxyInvocation;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 *    执行远程调用的时候一些元信息
 * @see JdkProxyInvocation
 * @author Vic.xu
 * @since 2025-08-27 9:21
 */
public class SrpcInvocation {

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * SrpcReference 元信息
     */
    private SrpcReference referenceAnnotation;

    /**
     * 方法名 和 SrpcMethod 元信息
     */
    private Map<String, SrpcInvocationMeta> methodMetaMap;

    private SrpcInvocationMeta defaultInvocationMeta;


    public SrpcInvocation(String interfaceName, SrpcReference referenceAnnotation) {
        Assert.notNull(interfaceName, "interfaceName can not be null");
        Assert.notNull(referenceAnnotation, "referenceAnnotation can not be null");
        this.interfaceName = interfaceName;
        this.referenceAnnotation = referenceAnnotation;
        defaultInvocationMeta = new SrpcInvocationMeta(referenceAnnotation);
        methodMetaMap = new HashMap<>();
        initMethodMetaMap();

    }

    /**
     * 初始化 指定方法 的元信息
     */
    private void initMethodMetaMap(){
        SrpcMethod[] methods = referenceAnnotation.methods();
        for (SrpcMethod method : methods) {
            SrpcInvocationMeta invocationMeta = new SrpcInvocationMeta(method, referenceAnnotation);
            methodMetaMap.put(method.name(), invocationMeta);
        }
    }

    /**
     * 获取指定方法 的元信息 (此处暂不支持方法重载)
     * @param methodName 方法名
     * @return InvocationMeta
     */
    public SrpcInvocationMeta getInvocationMeta(String methodName){
        SrpcInvocationMeta invocationMeta = methodMetaMap.get(methodName);
        if(invocationMeta == null){
            return defaultInvocationMeta;
        }
        return invocationMeta;
    }



}
