package cn.xuqiudong.mq.bridge.notify;

import cn.xuqiudong.mq.bridge.enums.OperationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 描述:
 *   消息通知入口
 * @author Vic.xu
 * @since 2026-02-05 14:46
 */
@Component
public class DataBridgeFailEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeFailEventPublisher.class);

    private DataBridgeFailHandler dataBridgeFailNotifier;


    public DataBridgeFailEventPublisher(ObjectProvider<DataBridgeFailHandler> dataBridgeFailNotifier) {
        this.dataBridgeFailNotifier = dataBridgeFailNotifier.getIfAvailable();
        if (this.dataBridgeFailNotifier == null) {
            LOGGER.warn("当前消息处理失败通知未开启");
        } else {
            LOGGER.info("当前消息处理失败通知已开启");
        }
    }

    /**
     * 发送消息处理失败通知
     */
    public void publish(OperationEnum operation, Integer messageId, String errorMsg) {
        publish(new FailContext(operation, messageId,  errorMsg));
    }

    /**
     * 发送消息处理失败通知
     */
    public void publish(FailContext failContext) {
        if (dataBridgeFailNotifier != null) {
            LOGGER.info("发送消息处理失败通知: {}", failContext.getMessageId());
            try {
                dataBridgeFailNotifier.onFail(failContext);

            } catch (Exception e) {
                LOGGER.warn("消息处理失败通知失败", e);
            }
        }
    }
}
