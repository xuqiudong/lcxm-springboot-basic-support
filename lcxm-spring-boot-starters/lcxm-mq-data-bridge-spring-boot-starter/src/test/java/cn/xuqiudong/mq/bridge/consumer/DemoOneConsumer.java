package cn.xuqiudong.mq.bridge.consumer;

import cn.xuqiudong.common.base.vo.BooleanWithMsg;
import cn.xuqiudong.common.util.JsonUtil;
import cn.xuqiudong.mq.bridge.annotation.ActionHandler;
import cn.xuqiudong.mq.bridge.consumer.model.DemoConsumerModel;
import cn.xuqiudong.mq.bridge.core.AbstractDataBridgeMessageConsumer;
import org.springframework.stereotype.Component;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-03-11 11:02
 */
@Component
public class DemoOneConsumer extends AbstractDataBridgeMessageConsumer {
    @Override
    public String module() {
        return "demo";
    }
    public static final String aa = "save";
    public DemoOneConsumer() {
        System.out.println("DemoOneConsumer init");


    }
    @ActionHandler(action = aa, messageType = DemoConsumerModel.class)
    public BooleanWithMsg save(DemoConsumerModel demoConsumerModel){
        if (demoConsumerModel.getAge() == 20) {
          //  return BooleanWithMsg.fail("年龄不能为20");
        }
        System.out.println("save DemoOneConsumer demoConsumerModel : age = " + demoConsumerModel.getAge() );
        JsonUtil.printJson(demoConsumerModel);
        return BooleanWithMsg.success();
    }
}
