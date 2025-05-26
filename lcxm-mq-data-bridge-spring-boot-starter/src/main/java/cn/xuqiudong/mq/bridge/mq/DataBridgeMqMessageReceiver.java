package cn.xuqiudong.mq.bridge.mq;

import cn.xuqiudong.mq.bridge.facade.DataBridgeMessageReceiverFacade;
import cn.xuqiudong.mq.bridge.helper.DataBridgeMqListenerSwitchHelper;
import cn.xuqiudong.mq.bridge.helper.DataBridgeGlobalConfigHelper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 描述:
 *      直接和mq对接的消息消费的处理类
 * @author Vic.xu
 * @since 2025-02-26 13:52
 */
@Component
public class DataBridgeMqMessageReceiver implements ChannelAwareMessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeMqMessageReceiver.class);

    private final DataBridgeGlobalConfigHelper dataBridgeGlobalSwitchHelper;

    private final DataBridgeMqListenerSwitchHelper dataBridgeCustomerListenerSwitchHelper;

    private final DataBridgeMessageReceiverFacade receiverDataBridgeFacade;

    public DataBridgeMqMessageReceiver(DataBridgeGlobalConfigHelper mqSwitchHelper, DataBridgeMqListenerSwitchHelper dataBridgeCustomerListenerSwitchHelper, DataBridgeMessageReceiverFacade receiverDataBridgeFacade) {
        this.dataBridgeGlobalSwitchHelper = mqSwitchHelper;
        this.dataBridgeCustomerListenerSwitchHelper = dataBridgeCustomerListenerSwitchHelper;
        this.receiverDataBridgeFacade = receiverDataBridgeFacade;
    }


    @Override
    public void onMessage(Message message, Channel channel) throws Exception{
        String msgId = message.getMessageProperties().getMessageId();
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        if (!dataBridgeGlobalSwitchHelper.isMqEnable()|| !dataBridgeGlobalSwitchHelper.isReceiveEnable()) {
            // 正常走不到这个逻辑，如果走到 则拒绝消息   且关闭mq 消费的监听
            LOGGER.info("MQ未启用或全局阻塞，跳过处理逻辑");
            dataBridgeCustomerListenerSwitchHelper.stopListener();
            rejectMessage(deliveryTag, msgId, channel);
            return;
        }
        LOGGER.info("receive message: {}", messageBody);
        try {
            // 将消息写入数据库 并 异步触发 处理消息
            receiverDataBridgeFacade.handle(msgId, messageBody);
            LOGGER.info("消息处理成功，已确认: {}", msgId);
            // 入库成功，手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            //消息处理失败，阻塞消息的消费
            LOGGER.error("消息[{}]处理失败，阻塞消息的消费: {}", msgId, e);
            dataBridgeCustomerListenerSwitchHelper.stopListener();
            rejectMessage(deliveryTag, msgId, channel);
        }

    }

    public void rejectMessage(long deliveryTag, String msgId, Channel channel) {
        // 处理失败，拒绝消息并重新入队
        try {
            LOGGER.warn("消息[{}]处理失败，拒绝消息并重新入队", msgId);
            channel.basicNack(deliveryTag, false, true);
        } catch (IOException e) {
            LOGGER.error("消息[{}]拒绝失败: {}", msgId, e);
        }
    }

}