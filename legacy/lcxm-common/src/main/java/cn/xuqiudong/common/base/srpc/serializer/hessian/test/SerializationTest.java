package cn.xuqiudong.common.base.srpc.serializer.hessian.test;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2024-12-10 11:24
 */
public class SerializationTest {


    public static void main(String[] args) {
        TestHessianMethod proxy = getProxy(TestHessianMethod.class);
        int add = proxy.add(1, 2);
        System.out.println(add);
        LocalDate now = proxy.now(LocalDateTime.now());
        System.out.println(now);
    }


    public static  <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new TestProxyInvocation(clazz));
    }




}
