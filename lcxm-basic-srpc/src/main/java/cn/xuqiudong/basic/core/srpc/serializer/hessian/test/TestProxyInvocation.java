package cn.xuqiudong.basic.core.srpc.serializer.hessian.test;

import cn.xuqiudong.basic.core.srpc.model.Invoker;
import cn.xuqiudong.basic.core.srpc.model.XqdRequest;
import cn.xuqiudong.basic.core.srpc.model.XqdResponse;
import cn.xuqiudong.basic.core.srpc.serializer.XqdSerializer;
import cn.xuqiudong.basic.core.srpc.serializer.hessian.Hessian2Serializer;
import cn.xuqiudong.basic.core.util.JsonUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 描述:基于jdk动态代理的调用远程服务的过程
 * @author Vic.xu
 * @@since  2024-06-25
 */
public class TestProxyInvocation implements InvocationHandler {



    private XqdSerializer serializer = new Hessian2Serializer();

    TestHessianMethodImpl testHessianMethodImpl = new TestHessianMethodImpl();

    private Invoker invoker;

    public TestProxyInvocation(Class clazz) {
        this.invoker = new Invoker();
        invoker.setInterfaceName(clazz.getName());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //方法名
        String methodName = method.getName();
        //参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        //组装好rpc的请求参数
        XqdRequest request = new XqdRequest(invoker.getInterfaceName(), methodName, parameterTypes, args);
        invoker.setXqdRequest(request);
        byte[] serialize = serializer.serialize(request);


        XqdRequest deserialize = serializer.deserialize(serialize, XqdRequest.class);
        JsonUtil.printJson(deserialize);
        Object invoke = TestHessianMethodImpl.class.getMethod(methodName, parameterTypes).invoke(testHessianMethodImpl, args);
        XqdResponse response = new XqdResponse();
        response.setData(invoke);
        byte[] serialize1 = serializer.serialize(response);
        XqdResponse xqdResponse = serializer.deserialize(serialize1, XqdResponse.class);
        return xqdResponse.getData();
   }
}


