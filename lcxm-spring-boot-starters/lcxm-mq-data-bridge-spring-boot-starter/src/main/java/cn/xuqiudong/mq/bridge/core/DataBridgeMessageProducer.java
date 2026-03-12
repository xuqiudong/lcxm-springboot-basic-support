package cn.xuqiudong.mq.bridge.core;

import cn.xuqiudong.basic.core.tool.Tools;
import cn.xuqiudong.basic.core.util.JsonUtil;
import cn.xuqiudong.mq.bridge.autoconfigure.DataBridgeProperties;
import cn.xuqiudong.mq.bridge.enums.SendStatusEnum;
import cn.xuqiudong.mq.bridge.facade.DataBridgeMessageSenderFacade;
import cn.xuqiudong.mq.bridge.model.DataBridgeSendMessage;
import cn.xuqiudong.mq.bridge.model.MessageContentWrapper;
import cn.xuqiudong.mq.bridge.service.DataBridgeSendMessageService;
import cn.xuqiudong.mq.bridge.vo.AbstractDataBridgeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 生产待发送的消息 的入口类
 *
 * @author Vic.xu
 * @since 2025-03-04 15:47
 */
@Component
public class DataBridgeMessageProducer {

    @Autowired
    private DataBridgeProperties dataBridgeProperties;

    @Autowired
    private DataBridgeSendMessageService dataBridgeSendMessageService;

    @Autowired
    protected DataBridgeMessageSenderFacade sendDataFacade;


    /**
     * 生成消息到消息表，并在当前事务提交后 执行异步消息发送到mq
     *
     * @param module 当前消息所属模块： 建议新建一个枚举，然后使用其字面量作为模块名称
     * @param action 当前消息所属动作： 建议新建一个枚举，然后使用其字面量作为动作名称   动作属于模块下， 动作和模块联合唯一，最终用于消息路由
     * @param data
     * @param <T>
     * @return
     */
    public <T extends AbstractDataBridgeVo> DataBridgeSendMessage produce(String module, String action, T data) {
        DataBridgeSendMessage message = new DataBridgeSendMessage();
        message.setStatus(SendStatusEnum.INITIAL);
        message.setMessageId(Tools.randomUuid());
        message.setModule(module);
        message.setAction(action);
        message.setQueueName(dataBridgeProperties.getSendQueue());
        message.setFlag(dataBridgeProperties.getProduceFlag());
        // 封装消息体
        MessageContentWrapper wrapper = new MessageContentWrapper(message, data);
        String content = JsonUtil.toJson(wrapper);
        message.setMessage(content);
        dataBridgeSendMessageService.save(message);
        // 在当前事务提交后 执行异步消息发送
        sendDataFacade.startSendAsyncWithTransactionCheck();
        return message;
    }
}
