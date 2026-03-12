package cn.xuqiudong.basic.srpc.proxy.jdk;


import cn.xuqiudong.basic.srpc.annotation.SrpcReference;
import cn.xuqiudong.basic.srpc.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * 描述: 基于jdk的动态代理工厂
 * @author Vic.xu
 * @since 2024-06-25
 */
public class JdkProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clazz, SrpcReference referenceAnnotation) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new JdkProxyInvocation(clazz, referenceAnnotation));
    }
}
