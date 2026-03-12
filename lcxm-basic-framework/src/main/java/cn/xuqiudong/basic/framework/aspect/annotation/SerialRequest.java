package cn.xuqiudong.basic.framework.aspect.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述: 串行化请求注解
 * @author Vic.xu
 * @since 2024-02-02 10:53
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface SerialRequest {

    /**
     * 锁名称(前缀：如表名/业务实体名)
     */
    String name();

    /**
     * 锁住的参数：对应方法中的参数,通过spel表达式取值
     * 如:
     *    获取参数id:   #id
     *    获取对象参数user中的id： #user.id
     *    获取map参数userMap中的id： #userMap['id']
     * ★ 不写此参数会锁住整个方法
     */
    String lockParameter() default "";

    /**
     * 获取锁的等待时间(秒) 默认10s, 10s后未能获取到锁，则直接抛出异常，
     */
    long waitTimeSeconds() default 10L;
}
