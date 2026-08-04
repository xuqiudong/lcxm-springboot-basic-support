package cn.xuqiudong.basic.third.inbound.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记某个方法参数不进入第三方入站调用日志。
 *
 * <p>只影响 {@link InboundLog} 切面的入参记录，不影响业务对象自身的 JSON 序列化规则。</p>
 *
 * @author Vic.xu
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface InboundLogIgnore {
}
