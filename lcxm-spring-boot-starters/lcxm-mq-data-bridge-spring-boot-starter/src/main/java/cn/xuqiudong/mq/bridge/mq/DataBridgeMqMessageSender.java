package cn.xuqiudong.mq.bridge.mq;

import cn.xuqiudong.basic.core.vo.BooleanWithMsg;
import cn.xuqiudong.mq.bridge.autoconfigure.DataBridgeProperties;
import cn.xuqiudong.mq.bridge.constant.DataBridgeConstant;
import cn.xuqiudong.mq.bridge.helper.DataBridgeGlobalConfigHelper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 直接和mq对接的消息发送 处理类
 * <p>
 * 为了同步顺序发送，使用 Future 阻塞等待(n秒)结果(可以不设置 ConfirmCallback)：依赖于下面这个配置
 * spring.rabbitmq.publisher-confirm-type=correlation
 * 交换机路由不到队列概率很低，一般在开发阶段就能发现的问题，这里防止万一
 * spring.rabbitmq.publisher-returns=true
 * </p>
 *
 * @author Vic.xu
 * @since 2025-02-26 14:50
 */
@Component
public class DataBridgeMqMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeMqMessageSender.class);

    private final RabbitTemplate rabbitTemplate;
    private final DataBridgeGlobalConfigHelper mqStatusSwitchHelper;
    private final DataBridgeProperties properties;

    public DataBridgeMqMessageSender(RabbitTemplate rabbitTemplate, DataBridgeGlobalConfigHelper mqStatusSwitchHelper, DataBridgeProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.mqStatusSwitchHelper = mqStatusSwitchHelper;
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback((returned) -> {
            MessageProperties messageProperties = returned.getMessage().getMessageProperties();
            // // 获取手动设置的 messageId
            String messageId = messageProperties.getMessageId();
            LOGGER.error("消息返回失败，消息ID: {}, 应答码: {}, 原因: {}, 交换机: {}, 路由键: {}, 消息: {}",
                    messageId,
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    new String(returned.getMessage().getBody(), StandardCharsets.UTF_8)
            );
        });
    }

    /**
     * 发送消息并进行指数退避重试
     */
    public BooleanWithMsg sendWithRetry(String msgId, String messageContent, String routingKey) {
        if (!mqStatusSwitchHelper.couldSend()) {
            return BooleanWithMsg.fail("全局阻塞或MQ未启用，跳过发送逻辑");
        }
        long delay = DataBridgeConstant.INITIAL_RETRY_DELAY;
        int attempt = 0;
        //最大尝试次数
        int maxAttempts = properties.getRetryCount();

        BooleanWithMsg result = BooleanWithMsg.fail("消息发送失败");
        while (attempt < maxAttempts) {
            result = sendMessage(msgId, messageContent, routingKey);
            if (result.isSuccess()) {
                LOGGER.info("{} 消息发送成功: {}", msgId, messageContent);
                return result;
            } else {
                attempt++;
                LOGGER.error("发送失败, {} 秒后重试 ({}/{})", delay / 1000, attempt, maxAttempts);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    LOGGER.error("发送消息失败", e);
                    result = BooleanWithMsg.fail(e.getMessage());
                    Thread.currentThread().interrupt();
                    return result;
                }
                delay = Math.min(delay * 2, DataBridgeConstant.MAX_RETRY_DELAY);
            }
        }

        LOGGER.error("发送失败，已达到最大重试次数: {}", msgId);
        return result;
    }

    /**
     * 发送消息 : 根据设置的超时时间同步阻塞N秒
     */
    public BooleanWithMsg sendMessage(String msgId, String messageContent, String routingKey) {
        if (!mqStatusSwitchHelper.couldSend()) {
            return BooleanWithMsg.fail("全局阻塞或MQ未启用，跳过发送逻辑");
        }
        CorrelationData correlationData = new CorrelationData(msgId);
        Message message = MessageBuilder.withBody(messageContent.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                // 手动设置 messageId
                .setMessageId(msgId)
                .build();
        try {
            rabbitTemplate.convertAndSend(properties.getExchange(), routingKey, message, correlationData);
            CorrelationData.Confirm confirmResult = correlationData.getFuture()
                    .get(properties.getSendConfirmTimeout(), TimeUnit.MILLISECONDS);
            if (Boolean.TRUE.equals(confirmResult.isAck())) {
                LOGGER.info("{} 消息发送成功", msgId);
                return BooleanWithMsg.success();
            } else {
                LOGGER.error("{} 消息未被确认: {}", msgId, confirmResult.getReason());
                return BooleanWithMsg.fail("消息未被确认");
            }
        } catch (Exception e) {
            LOGGER.error("{} 消息发送失败: {}", msgId, e.getMessage(), e);
            return BooleanWithMsg.fail("消息发送失败: " + e.getMessage());
        }
    }


}
