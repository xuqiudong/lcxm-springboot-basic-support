package cn.xuqiudong.common.base.srpc.proxy.jdk;

import cn.xuqiudong.common.base.srpc.annotation.SrpcReference;
import cn.xuqiudong.common.base.srpc.model.Invoker;
import cn.xuqiudong.common.base.srpc.model.SrpcInvocation;
import cn.xuqiudong.common.base.srpc.model.XqdRequest;
import cn.xuqiudong.common.base.srpc.protocol.HttpProtocol;
import cn.xuqiudong.common.base.srpc.protocol.Protocol;
import cn.xuqiudong.common.base.srpc.proxy.ProxyFactory;
import cn.xuqiudong.common.base.srpc.serializer.XqdSerializer;
import cn.xuqiudong.common.base.srpc.serializer.hessian.Hessian2Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 描述:基于jdk动态代理的调用远程服务的过程
 * @author Vic.xu
 * @@since  2024-06-25
 */
public class JdkProxyInvocation implements InvocationHandler {



    private XqdSerializer serializer = new Hessian2Serializer();

    Protocol protocol = new HttpProtocol();

    private String interfaceName;

    private SrpcInvocation srpcInvocation;


    public JdkProxyInvocation(Class clazz, SrpcReference referenceAnnotation) {
        this.interfaceName = clazz.getName();
        this.srpcInvocation = new SrpcInvocation(interfaceName, referenceAnnotation);
    }

    private Invoker buildInvoker(XqdRequest request) {
        Invoker invoker = new Invoker();
        invoker.setInterfaceName(interfaceName);
        invoker.setInvocation(srpcInvocation);
        invoker.setXqdRequest(request);
        return invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (ProxyFactory.isIgnoreMethod(method)) {
            return method.invoke(proxy, args);
        }
        //方法名
        String methodName = method.getName();
        //参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        //组装好rpc的请求参数
        XqdRequest request = new XqdRequest(interfaceName, methodName, parameterTypes, args);
        Invoker invoker = buildInvoker(request);

        //发送方法到远程服务
        Object result = protocol.send(invoker);

        //处理返回的结果： 此处使用序列化/发序列化对方法的返回值再次处理，是为了防止json反序列化时多层嵌套，内层泛型丢失 FIXME
        if (!serializer.selfDescribed()) {
            byte[] bytes = serializer.serialize(result);
            result = serializer.deserialize(bytes, method.getReturnType());
        }
        return result;
   }
}


