package cn.xuqiudong.basic.third.inbound.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要记录入参、出参和异常的第三方入站业务方法。
 *
 * @author Vic.xu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InboundLog {

    /**
     * 第三方标识。
     *
     * <p>固定第三方接口可直接声明；通用入口可留空，并在业务代码中通过
     * InboundInvokeContextHolder 写入 thirdCode。</p>
     */
    String thirdCode() default "";

    /**
     * 业务操作名称，例如订单推送、库存同步。
     */
    String operation() default "";

    /**
     * 是否记录方法入参。
     */
    boolean request() default true;

    /**
     * 是否记录方法出参。
     */
    boolean response() default true;

    /**
     * 是否记录异常堆栈。
     */
    boolean exceptionStack() default true;
}
