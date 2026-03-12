package cn.xuqiudong.basic.srpc.provider;

import cn.xuqiudong.basic.core.exception.CommonException;
import cn.xuqiudong.basic.srpc.model.XqdRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述: 服务端持有spring中的bean引用， 即标注为@XqdService的spring中的bean； XqdServiceHolder  是一个单例bean
 * @author Vic.xu
 * @since  2024-06-25
 */
public class XqdServiceHolder {
    private static XqdServiceHolder instance = new XqdServiceHolder();

    /**
     * 持有bean的map, key 为 interfaceName , value 为其实例
     */
    private static Map<String, Object> holder = new ConcurrentHashMap<>();


    private XqdServiceHolder() {
    }

    public static void putBean(String key, Object bean) {
        holder.put(key, bean);
    }

    private static Object fetchBean(String key) {
        return holder.get(key);
    }


    /**
     * 根据XqdRequest  查找到服务端对应的service，然后执行对应的method方法
     * @param body XqdRequest
     * @return Object
     */
    public static Object invokeMethod(XqdRequest body) {
        Object service = fetchBean(body.getInterfaceName());
        if (service == null) {
            throw new CommonException("Processing service that does not exist");
        }
        try {
            Method method = service.getClass().getMethod(body.getMethodName(), body.getParamTypes());
            Object data = method.invoke(service, body.getParams());
            return data;
        } catch (InvocationTargetException e) {
            // 这是反射的内部的方法报的真正的异常信息
            Throwable targetException = e.getTargetException();
            throw new CommonException("通过反射执行提供端方法异常:" + targetException.getMessage(), targetException);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(e.getMessage(), e);
        }


    }


}
