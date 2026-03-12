package cn.xuqiudong.mq.bridge.controller;

import cn.xuqiudong.common.base.controller.BaseGenericController;
import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.mq.bridge.enums.SendStatusEnum;
import cn.xuqiudong.mq.bridge.model.DataBridgeSendMessage;
import cn.xuqiudong.mq.bridge.service.DataBridgeSendMessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能: :发送到mq的消息表 控制层
 *
 * @author Vic.xu
 * @since 2025-03-03 11:11
 */
@RestController
@RequestMapping("/mq-data-bridge/send")
public class DataBridgeSendMessageController extends BaseGenericController<DataBridgeSendMessageService, DataBridgeSendMessage, Integer> {

    /**
     * 修改数据
     */
    @PostMapping("/amend")
    public BaseResponse<Boolean> amend(DataBridgeSendMessage entity){
        entity.setStatus(SendStatusEnum.AMENDED);
        return BaseResponse.success(service.update(entity) > 0);
    }
    /**
     * 丢弃数据
     */
    @PostMapping("/discard")
    public BaseResponse<Boolean> discard(Integer id){
        DataBridgeSendMessage entity = new DataBridgeSendMessage();
        entity.setId(id);
        entity.setStatus(SendStatusEnum.DISCARDED);
        return BaseResponse.success(service.update(entity) > 0);
    }

}
