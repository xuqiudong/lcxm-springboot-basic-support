package cn.xuqiudong.common.base.srpc.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 描述:
 *  在标注为spring的bean的同时，会被追加到注册中心，但是由于是simpleRpc,所以暂不处理
 * @author Vic.xu
 * @since 2024-06-25 10:07
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface SrpcService {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
