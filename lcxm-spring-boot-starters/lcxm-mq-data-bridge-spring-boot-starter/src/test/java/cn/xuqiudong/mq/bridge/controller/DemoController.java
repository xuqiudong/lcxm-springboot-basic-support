package cn.xuqiudong.mq.bridge.controller;

import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.framework.tool.Tools;
import cn.xuqiudong.mq.bridge.consumer.model.DemoConsumerModel;
import cn.xuqiudong.mq.bridge.core.DataBridgeMessageProducer;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-03-12 15:04
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DataBridgeMessageProducer dataBridgeMessageProducer;

    @GetMapping("/send")
    public BaseResponse<DemoConsumerModel> send(DemoConsumerModel model) {
        model.setId(Tools.randomUuid());
        if (model.getAge() == 0) {
            model.setAge(10 + RandomUtils.nextInt(20));
        }
        if (model.getName() == null) {
            model.setName(LocalTime.now().toString());
        }
        dataBridgeMessageProducer.produce("demo", "save", model);
        return BaseResponse.success(model);
    }
}
