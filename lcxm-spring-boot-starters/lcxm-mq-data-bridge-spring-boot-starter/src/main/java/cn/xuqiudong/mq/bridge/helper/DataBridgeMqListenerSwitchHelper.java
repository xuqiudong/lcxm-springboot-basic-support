package cn.xuqiudong.mq.bridge.helper;

import cn.xuqiudong.mq.bridge.event.DataBridgeMqEventHandler;
import cn.xuqiudong.mq.bridge.event.DataBridgeMqListenerStartEvent;
import cn.xuqiudong.mq.bridge.event.DataBridgeMqListenerStopEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * mq 消息监听(消费) 开关 工具类
 * <p>
 * 正常写法下，
 * 由于本类依赖于 SimpleMessageListenerContainer，
 * 而SimpleMessageListenerContainer 中需要注入DataBridgeReceiver 处理消息，
 * 而DataBridgeReceiver 中在判断需要阻塞消息的时候需要 使用本类 进行启动和关闭 消息监听
 * 从而导致了三者的循环依赖。
 * 虽然@Lazy 可以解决，但总归不符合spring 的推荐写法。
 * 所以，修改为 事件驱动的方式，解耦 本类和 SimpleMessageListenerContainer的关系
 * </p>
 *
 * @author Vic.xu
 * @see DataBridgeMqEventHandler
 * @since 2025-02-27 11:27
 */
@Component
public class DataBridgeMqListenerSwitchHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeMqListenerSwitchHelper.class);

    private final ApplicationEventPublisher eventPublisher;

    public DataBridgeMqListenerSwitchHelper(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void switchListener(boolean isStart) {
        if (isStart) {
            startListener();
        } else {
            stopListener();
        }
    }

    /**
     * 启动消费监听器
     */
    public void startListener() {
        LOGGER.info("publish StartMqListenerEvent");
        eventPublisher.publishEvent(new DataBridgeMqListenerStartEvent(this));

    }

    /**
     * 停止消费监听器
     */
    public void stopListener() {
        LOGGER.info("publish StopMqListenerEvent");
        eventPublisher.publishEvent(new DataBridgeMqListenerStopEvent(this));
    }
}
