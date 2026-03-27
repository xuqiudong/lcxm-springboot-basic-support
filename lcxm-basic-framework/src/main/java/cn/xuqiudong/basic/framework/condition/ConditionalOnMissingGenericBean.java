package cn.xuqiudong.basic.framework.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * 注解：条件注册泛型 Bean
 *
 * 使用此注解时，需要指定接口类型和泛型类型。
 * 示例：
 *
 * @ConditionalOnMissingGenericBean(beanInterface = Code2TextResolver.class, genericType = UserCode2Text.class)
 * @author Vic.xu
 * @since 2026-03-27 10:49
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnMissingGenericBeanCondition.class)
public @interface ConditionalOnMissingGenericBean {
    /**
     * 要检查的接口类型，例如 Code2TextResolver
     */
    Class<?> beanInterface();

    /**
     * 接口泛型类型，例如 UserCode2Text
     */
    Class<?> genericType();
}
