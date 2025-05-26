package cn.xuqiudong.mq.bridge.event;

import org.springframework.context.ApplicationEvent;

/**
 * 描述:
 *      开启mq监听 事件
 * @since 2025-03-07 15:49
 */
public class DataBridgeMqListenerStartEvent extends ApplicationEvent {


    public DataBridgeMqListenerStartEvent(Object source) {
        super(source);
    }
}
