package cn.xuqiudong.mq.bridge;

import cn.xuqiudong.basic.framework.tool.Tools;
import cn.xuqiudong.mq.bridge.autoconfigure.DataBridgeProperties;
import cn.xuqiudong.mq.bridge.consumer.model.DemoConsumerModel;
import cn.xuqiudong.mq.bridge.core.DataBridgeMessageProducer;
import cn.xuqiudong.mq.bridge.helper.DataBridgeGlobalConfigHelper;
import cn.xuqiudong.mq.bridge.helper.DataBridgeMqListenerSwitchHelper;
import cn.xuqiudong.mq.bridge.mq.DataBridgeMqMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-02-26 15:04
 */
@Component
public class AfterStartup implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AfterStartup.class);

    @Autowired
    private DataBridgeMqMessageSender messageSenderService;

    @Autowired
    private DataBridgeProperties mqDataBridgeProperties;

    @Autowired
    private DataBridgeGlobalConfigHelper dataBridgeGlobalSwitchHelper;

    @Autowired
    private DataBridgeMqListenerSwitchHelper mqCustomerListenerSwitchHelper;

    @Autowired
    private DataBridgeMessageProducer dataBridgeMessageProducer;

    public void sendMsg(String msg) throws InterruptedException {
        String queue = mqDataBridgeProperties.getReceiveQueue();
        String routingKey = mqDataBridgeProperties.getReceiveRoutingKey();
            TimeUnit.SECONDS.sleep(5);
            logger.info("ready to send message to queue: {}!!!", queue);
            messageSenderService.sendMessage(Tools.randomUuid(), msg + " " + LocalTime.now(), routingKey);


    }

    @Override
    public void run(String... args) throws Exception {
//        sendMsg("未监听器时 初始 发送消息");
//        mqCustomerListenerSwitchHelper.stopListener();
//        sendMsg("关闭监听器后发送消息");
//        mqCustomerListenerSwitchHelper.startListener();
//        sendMsg("启动监听器后发送消息");
        for (int i = 0; i < 5; i++) {
            sendDemo(i);
            TimeUnit.MILLISECONDS.sleep(200);
        }
    }

    private void sendDemo(int i){
        DemoConsumerModel demoConsumerModel = new DemoConsumerModel();
        demoConsumerModel.setId(Tools.randomUuid());
        demoConsumerModel.setName("Vic" + LocalTime.now());
        demoConsumerModel.setAge(18 + i);
        dataBridgeMessageProducer.produce("demo", "save", demoConsumerModel);
    }
}
