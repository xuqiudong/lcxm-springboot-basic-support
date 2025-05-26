package cn.xuqiudong.common.base.srpc.reference;

import cn.xuqiudong.common.base.srpc.annotation.SrpcReference;
import cn.xuqiudong.common.base.srpc.proxy.ProxyFactory;
import cn.xuqiudong.common.base.srpc.proxy.jdk.JdkProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * 描述:为远程接口生成代理类的bean工厂：
 *   扫描每个bean生成时候的内部的某个filed是否被标注为{@link SrpcReference}
 *   实现implements FactoryBean<T>接口，调用的时候，便会主动去找getObject()方法了，返回的是Bean，而不是这个Factory本身。
 * @author Vic.xu
 * @date 2022-02-28 17:16
 */
public class XqdBeanFactory implements FactoryBean<Object> {

    /**
     * interfaceClass  字段名
     */
    public static final String INTERFACE_CLASS_FIELD_NAME = "interfaceClass";

    /**
     * init方法名
     */
    public static final String INIT_METHOD_NAME = "init";

    ProxyFactory factory = new JdkProxyFactory();

    /**
     * 接口
     */
    private Class<?> interfaceClass;


    private Object bean;


    @Override
    public Object getObject() throws Exception {
        return bean;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    /**
     * 初始化方法
     */
    private void init(){
        this.bean = factory.getProxy(interfaceClass);
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

}
