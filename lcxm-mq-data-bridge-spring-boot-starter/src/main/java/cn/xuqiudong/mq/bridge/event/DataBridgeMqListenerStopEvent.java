package cn.xuqiudong.mq.bridge.event;

import org.springframework.context.ApplicationEvent;

/**
 * 描述:
 *      停止mq监听 事件
 * @since 2025-03-07 15:49
 */
public class DataBridgeMqListenerStopEvent extends ApplicationEvent {


    public DataBridgeMqListenerStopEvent(Object source) {
        super(source);
    }
}
