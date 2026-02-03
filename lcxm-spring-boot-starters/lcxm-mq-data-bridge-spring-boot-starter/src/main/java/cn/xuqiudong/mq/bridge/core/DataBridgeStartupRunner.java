package cn.xuqiudong.mq.bridge.core;

import cn.xuqiudong.mq.bridge.facade.DataBridgeMessageReceiverFacade;
import cn.xuqiudong.mq.bridge.facade.DataBridgeMessageSenderFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 描述:
 *      项目启动后 触发 异步发送和异步消费动作
 * @author Vic.xu
 * @since 2025-03-07 15:09
 */
@Component
public class DataBridgeStartupRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeStartupRunner.class);

    // 改成懒加载，确保返回代理  AopContext.currentProxy() 必须在代理方法调用的上下文中才能拿到。
    private final DataBridgeMessageReceiverFacade receiverDataBridgeFacade;

    // 改成懒加载，确保返回代理
    private final DataBridgeMessageSenderFacade sendDataBridgeFacade;

    public DataBridgeStartupRunner(@Lazy DataBridgeMessageReceiverFacade receiverDataBridgeFacade,
                                   @Lazy DataBridgeMessageSenderFacade sendDataBridgeFacade) {
        this.receiverDataBridgeFacade = receiverDataBridgeFacade;
        this.sendDataBridgeFacade = sendDataBridgeFacade;
    }

    /**
     * 比 CommandLineRunner 时机晚  确保 启动完成 完成AOP
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        LOGGER.info("start consumer  message async after application start");
        receiverDataBridgeFacade.startConsumerAsync();
        LOGGER.info("start send message async after application start ");
        sendDataBridgeFacade.startSendAsync();
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
