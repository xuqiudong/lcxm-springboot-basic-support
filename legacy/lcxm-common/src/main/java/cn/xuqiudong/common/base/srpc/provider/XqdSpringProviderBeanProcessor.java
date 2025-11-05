package cn.xuqiudong.common.base.srpc.provider;

import cn.xuqiudong.common.base.srpc.annotation.SrpcService;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * 描述:   对于提供者service注册到spring之后的其他处理：如持有service 或添加到注册中心等
 * <p>
 *     BeanPostProcessor，然后对所有的bean进行一个初始化之前/之后的代理
 *
 * </p>
 * @author Vic.xu
 * @date 2022-02-24 17:05
 */
public class XqdSpringProviderBeanProcessor implements BeanPostProcessor {


    private static org.slf4j.Logger logger = LoggerFactory.getLogger(XqdSpringProviderBeanProcessor.class);

    /**
     * 在bean初始化完成之后的后置处理器， 若被标志为 {@link SrpcService}  则向注册中心开始注册
     * @param bean bean
     * @param beanName beanName
     * @return bean
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取最终目标class（防止存在代理对象的时候 获取到的时候代理Proxy）,故此处不使用  bean.getClass();
        Class beanClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!beanClass.isAnnotationPresent(SrpcService.class)) {
            return bean;
        }
        //暂未处理多接口的问题 FIXME
        Class[] interfaces = beanClass.getInterfaces();
        if(interfaces.length == 0){
            logger.error("被标注为@SrpcService的bean: {}，未实现相关接口, 故不注册到XqdServiceHolder", beanName);
            return bean;
        }

        String interfaceName = beanClass.getInterfaces()[0].getName();
        // 保留对其引用
        XqdServiceHolder.putBean(interfaceName, bean);
        //此处不需要注册到注册中心
        return bean;
    }
}
