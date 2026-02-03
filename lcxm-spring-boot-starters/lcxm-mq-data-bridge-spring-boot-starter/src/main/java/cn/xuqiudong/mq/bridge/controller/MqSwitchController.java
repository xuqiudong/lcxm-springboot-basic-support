package cn.xuqiudong.mq.bridge.controller;

import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.mq.bridge.facade.DataBridgeMessageReceiverFacade;
import cn.xuqiudong.mq.bridge.facade.DataBridgeMessageSenderFacade;
import cn.xuqiudong.mq.bridge.helper.DataBridgeGlobalConfigHelper;
import cn.xuqiudong.mq.bridge.helper.DataBridgeMqListenerSwitchHelper;
import cn.xuqiudong.mq.bridge.vo.DataBridgeEnableVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 * 一些开关控制
 *
 * @author Vic.xu
 * @since 2025-02-26 13:53
 */
@RestController
@RequestMapping("/mq-data-bridge/config")
public class MqSwitchController {

    @Autowired
    private DataBridgeGlobalConfigHelper mqStatusSwitchHelper;

    @Autowired
    private DataBridgeMqListenerSwitchHelper dataBridgeMqListenerSwitchHelper;

    @Autowired
    private DataBridgeMessageSenderFacade dataBridgeMessageSenderFacade;

    @Autowired
    private DataBridgeMessageReceiverFacade dataBridgeMessageReceiverFacade;

    /**
     * 获取所有的开关状态 概览
     */
    @GetMapping("/summary")
    public BaseResponse<DataBridgeEnableVO> allStatus() {
        DataBridgeEnableVO dataBridgeEnableVO = mqStatusSwitchHelper.allStatus();
        return BaseResponse.success(dataBridgeEnableVO);
    }

    /**
     * 设置开关
     */
    @GetMapping("/set")
    public BaseResponse<Void> setSwitch(String name, boolean enabled) {
        if (StringUtils.isBlank(name)) {
            return BaseResponse.error("name is blank");
        }
        switch (name) {
            case "mq":
                mqStatusSwitchHelper.setMqEnable(enabled);
                break;
            case "send":
                mqStatusSwitchHelper.setSendEnable(enabled);
                break;
            case "receive":
                mqStatusSwitchHelper.setReceiveEnable(enabled);
                break;
            case "consumer":
                mqStatusSwitchHelper.setConsumerEnable(enabled);
                break;
            default:
                return BaseResponse.error("name is not support");

        }
        trigger();
        return BaseResponse.success();
    }

    public void trigger(){
        DataBridgeEnableVO dataBridgeEnableVO = mqStatusSwitchHelper.allStatus();
        // 触发监听开关
        dataBridgeMqListenerSwitchHelper.switchListener(dataBridgeEnableVO.isReceiveEnable());
        // 触发发送
        if (dataBridgeEnableVO.isSendEnable()) {
            dataBridgeMessageSenderFacade.startSendAsync();
        }
        //触发消费
        if (dataBridgeEnableVO.isConsumerEnable()) {
            dataBridgeMessageReceiverFacade.startConsumerAsync();
        }

    }

}