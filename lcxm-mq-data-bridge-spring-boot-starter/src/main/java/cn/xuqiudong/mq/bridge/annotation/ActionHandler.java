package cn.xuqiudong.mq.bridge.annotation;

import cn.xuqiudong.mq.bridge.vo.AbstractDataBridgeVo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 * 消息消费的处理类
 *
 * @author Vic.xu
 * @since 2025-03-07 11:12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionHandler {
    /**
     * 需要匹配的 act
     */
    String action();

    /**
     * 需要message 解析成的类型
     */
    Class<? extends AbstractDataBridgeVo> messageType();
}
