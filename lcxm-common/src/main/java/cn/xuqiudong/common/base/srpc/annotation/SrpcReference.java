package cn.xuqiudong.common.base.srpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * 描述:
 *  在被spring扫描的bean中的某个属性(rpc接口)被标注为 @SrpcReference，为其其动态生成代理类，
 *   注册到spring，然后由于此注解被标注为Autowired，最终属性被spring注入
 * @author Vic.xu
 * @since 2024-06-25 10:10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Autowired
public @interface SrpcReference {
}
