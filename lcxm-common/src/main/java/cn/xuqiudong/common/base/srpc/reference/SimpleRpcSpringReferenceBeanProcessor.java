package cn.xuqiudong.common.base.srpc.reference;

import cn.xuqiudong.common.base.srpc.annotation.SrpcReference;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 * spring中通过{@link SrpcReference} 引用的类的动态注入
 *
 * @author Vic.xu
 * @since 2024-06-25 10:13
 */
public class SimpleRpcSpringReferenceBeanProcessor implements BeanFactoryPostProcessor, BeanClassLoaderAware, ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(SimpleRpcSpringReferenceBeanProcessor.class);
    /**
     * 通过BeanClassLoaderAware 注入
     */
    private ClassLoader classLoader;

    /**
     * 通过ApplicationContextAware 注入
     */
    private ApplicationContext applicationContext;

    /**
     * 保存引用的bean
     */
    private final Map<String, BeanDefinition> XQD_REFERENCE_BEAN_MAP = new HashMap<>();

    /**
     * 此时bean尚未初始化
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //获得所有的定义的bean, 遍历bean中的字段是否通过SrpcReference注解 注入，是的话 则构件动态代理类
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            //获得的bean的定义
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            //如果bean不为空，则
            if (beanClassName != null) {
                //根据beanClassName解析出Class， 然后遍历field， 若被SrpcReference标记，则通过BeanDefinitionBuilder创建动态代理类
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.classLoader);
                ReflectionUtils.doWithFields(clazz, this::fieldCallback);
            }
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        XQD_REFERENCE_BEAN_MAP.forEach((beanName, beanDefinition) -> {
            if (applicationContext.containsBean(beanName)) {
                logger.info("{} 已经注册到spring上下文", beanName);
                return;
            }
            registry.registerBeanDefinition(beanName, beanDefinition);
            logger.info("成功注册 XqdReference bean：{}到spring", beanName);
        });
    }

    private void fieldCallback(Field field) {
        SrpcReference annotation = AnnotationUtils.getAnnotation(field, SrpcReference.class);
        if (annotation == null) {
            return;
        }
        Class<?> fieldType = field.getType();
        // 检查Spring上下文中是否已有该接口的实现
        if (hasExistingImplementation(fieldType)) {
            logger.info("Interface {} already has implementation, skip srpc proxy creation", fieldType.getName());
            return;
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(XqdBeanFactory.class);
        builder.setInitMethodName(XqdBeanFactory.INIT_METHOD_NAME);
        builder.addPropertyValue(XqdBeanFactory.INTERFACE_CLASS_FIELD_NAME, field.getType());
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        XQD_REFERENCE_BEAN_MAP.put(field.getName(), beanDefinition);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 检查接口是否已有实现Bean
     * 可以考虑使用ResolvableType 满足后续可能存在的泛型接口 / 抽象类/接口继承 的场景
     */
    private boolean hasExistingImplementation(Class<?> interfaceType) {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(interfaceType);
        return ArrayUtils.isNotEmpty(beanNamesForType);
    }


}
