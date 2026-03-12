package cn.xuqiudong.basic.srpc.proxy;

import cn.xuqiudong.basic.srpc.annotation.SrpcReference;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 描述: 代理类生成工厂
 * @author Vic.xu
 * @since 2024-06-25
 */
public interface ProxyFactory {

    /**
     * 忽略的方法，不代理
     * @see Object
     */
    Set<String> IGNORE_RPC_METHODS = Stream.of(
            "wait",
                    "equals",
                    "toString",
                    "hashCode",
                    "getClass",
                    "notify",
                    "notifyAll")
            .collect(Collectors.toSet());

    static boolean isIgnoreMethod(Method method) {
        return IGNORE_RPC_METHODS.contains(method.getName());
    }

    /**
     * 获得代理类
     * @param clazz 接口class
     * @param referenceAnnotation SrpcReference注解 方便传递 元数据
     * @param <T>
     * @return 代理类
     */
    <T> T getProxy(Class<T> clazz, SrpcReference referenceAnnotation);



}
