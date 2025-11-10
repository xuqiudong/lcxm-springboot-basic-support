package cn.xuqiudong.common.base.srpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 *   支持方法级别的控制
 * @see SrpcReference#methods()
 * @author Vic.xu
 * @since 2025-08-27 9:10
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Inherited
public @interface SrpcMethod {
    /**
     * 作用的方法名
     */
    String name();

    /**
     * 区分调用不同的服务
     */
    String serviceCode() default "";

    /**
     * 超时时间
     */
    int timeout() default -1;
}
