package cn.xuqiudong.basic.framework.condition;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Description: 通用 Condition
 *
 * <p>
 * 用于判断 Spring 容器中是否已经存在某个接口的泛型实现。
 * <p>
 * 特点：
 * 1. 不调用 beanFactory.getBean()，避免提前初始化 Bean 导致字段注入为 null。
 * 2. 支持接口类型和泛型类型可配置。
 * 3. 可用于自动装配默认实现时的条件判断。
 * <p>
 * 使用方式：
 * 在自定义注解上使用 @Conditional(OnMissingGenericBeanCondition.class) 并提供接口类型和泛型类型：
 *
 * @see ConditionalOnMissingGenericBean {
 *  * * Class<?> beanInterface();  // 要检查的接口类型
 *  * * Class<?> genericType();    // 接口泛型
 *  * * }
 * </p>
 *
 * @author Vic.xu
 *
 * @since 2026-03-27 10:50
 */
public class OnMissingGenericBeanCondition implements Condition {

    @Override
    public boolean matches(@NonNull ConditionContext context, AnnotatedTypeMetadata metadata) {

        // 获取注解属性
        Map<String, Object> attrs = metadata.getAnnotationAttributes(ConditionalOnMissingGenericBean.class.getName());
        if (attrs == null || !attrs.containsKey("beanInterface") || !attrs.containsKey("genericType")) {
            // 未指定接口或泛型，则默认注册
            return true;
        }

        Class<?> beanInterface = (Class<?>) attrs.get("beanInterface");
        Class<?> genericType = (Class<?>) attrs.get("genericType");

        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        if (beanFactory == null) {
            return true;
        }

        // 获取容器中所有实现指定接口的 Bean 名称
        String[] beanNames = beanFactory.getBeanNamesForType(beanInterface, true, false);

        for (String name : beanNames) {
            // 不实例化 Bean  防止bean提前初始化引起一些注入问题
            Class<?> beanClass = beanFactory.getType(name);
            if (beanClass == null) {
                continue;
            }

            // 遍历 Bean 所实现的接口
            Type[] interfaces = beanClass.getGenericInterfaces();
            for (Type face : interfaces) {
                if (face instanceof ParameterizedType pt) {
                    Type raw = pt.getRawType();
                    Type arg = pt.getActualTypeArguments()[0];

                    if (raw instanceof Class<?> rawClass) {
                        // 检查接口类型是否匹配，并且泛型类型匹配
                        if (beanInterface.isAssignableFrom(rawClass) && arg == genericType) {
                            // 已经存在该接口的泛型实现，不注册默认 Bean
                            return false;
                        }
                    }
                }
            }
        }

        // 没有找到匹配 Bean，可以注册默认 Bean
        return true;
    }
}
