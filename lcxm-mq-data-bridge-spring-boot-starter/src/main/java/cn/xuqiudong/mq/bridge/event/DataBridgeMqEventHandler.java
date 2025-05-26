package cn.xuqiudong.mq.bridge.event;

import cn.xuqiudong.mq.bridge.helper.DataBridgeGlobalConfigHelper;
import cn.xuqiudong.mq.bridge.helper.DataBridgeMqListenerSwitchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * MQ 监听器事件处理器
 *
 * @author Vic.xu
 * @see DataBridgeMqListenerSwitchHelper
 * @since 2025-03-07 15:58
 */
@Component
public class DataBridgeMqEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeMqEventHandler.class);

    private final SimpleMessageListenerContainer messageListenerContainer;

    private final DataBridgeGlobalConfigHelper dataBridgeGlobalConfigHelper;

    public DataBridgeMqEventHandler(SimpleMessageListenerContainer simpleMessageListenerContainer, DataBridgeGlobalConfigHelper dataBridgeGlobalConfigHelper) {
        this.messageListenerContainer = simpleMessageListenerContainer;
        this.dataBridgeGlobalConfigHelper = dataBridgeGlobalConfigHelper;
    }

    /**
     * 启动消费监听器
     */
    @SuppressWarnings("unused")
    @EventListener
    public void startListener(DataBridgeMqListenerStartEvent event) {
        LOGGER.info("MQ 启动监听器");
        if (!messageListenerContainer.isRunning()) {
            messageListenerContainer.start();
            dataBridgeGlobalConfigHelper.setReceiveEnable(true);
        } else {
            LOGGER.info("MQ 监听器已经启动， 无需再次启动");
        }
    }

    /**
     * 停止消费监听器
     */
    @SuppressWarnings("unused")
    @EventListener
    public void stopListener(DataBridgeMqListenerStopEvent event) {
        LOGGER.info("MQ 停止监听器");
        if (messageListenerContainer.isRunning()) {
            messageListenerContainer.stop();
            dataBridgeGlobalConfigHelper.setReceiveEnable(false);
        } else {
            LOGGER.info("MQ 监听器已经停止， 无需再次停止");
        }
    }
}
