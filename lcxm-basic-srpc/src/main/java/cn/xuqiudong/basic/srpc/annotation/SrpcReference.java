package cn.xuqiudong.basic.srpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 * 在被spring扫描的bean中的某个属性(rpc接口)被标注为 @SrpcReference，为其其动态生成代理类，
 * 注册到spring，然后由于此注解被标注为Autowired，最终属性被spring注入
 *
 * @author Vic.xu
 * @since 2024-06-25 10:10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Autowired
public @interface SrpcReference {

    /**
     * 区分调用不同的服务
     */
    String serviceCode() default "";

    /**
     * 超时时间
     */
    int timeout() default -1;

    /**
     * 接口方法的一些设置: 如超时,或指定具体服务
     *
     * @return
     */
    SrpcMethod[] methods() default {};
}
