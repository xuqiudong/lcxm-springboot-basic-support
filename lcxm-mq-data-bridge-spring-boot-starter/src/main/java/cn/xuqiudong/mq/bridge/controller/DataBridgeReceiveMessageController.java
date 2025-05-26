package cn.xuqiudong.mq.bridge.controller;

import cn.xuqiudong.common.base.controller.BaseGenericController;
import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.mq.bridge.enums.ReceiveStatusEnum;
import cn.xuqiudong.mq.bridge.enums.SendStatusEnum;
import cn.xuqiudong.mq.bridge.model.DataBridgeSendMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.xuqiudong.common.base.controller.BaseController;

import cn.xuqiudong.mq.bridge.service.DataBridgeReceiveMessageService;  
import cn.xuqiudong.mq.bridge.model.DataBridgeReceiveMessage;

/**
 *功能: :接收自mq的消息表 控制层
 * @author Vic.xu
 * @since  2025-03-03 11:11
 */
@RestController
@RequestMapping("/mq-data-bridge/receive")
public class DataBridgeReceiveMessageController extends BaseGenericController<DataBridgeReceiveMessageService, DataBridgeReceiveMessage, Integer> {

    /**
     * 修改数据
     */
    @PostMapping("/amend")
    public BaseResponse<Boolean> amend(DataBridgeReceiveMessage entity){
        entity.setStatus(ReceiveStatusEnum.AMENDED);
        return BaseResponse.success(service.update(entity) > 0);
    }
    /**
     * 丢弃数据
     */
    @PostMapping("/discard")
    public BaseResponse<Boolean> discard(Integer id){
        DataBridgeReceiveMessage entity = new DataBridgeReceiveMessage();
        entity.setId(id);
        entity.setStatus(ReceiveStatusEnum.DISCARDED);
        return BaseResponse.success(service.update(entity) > 0);
    }
}
