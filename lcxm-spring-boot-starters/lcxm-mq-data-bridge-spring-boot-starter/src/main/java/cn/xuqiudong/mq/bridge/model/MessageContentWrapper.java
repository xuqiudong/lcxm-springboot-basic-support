package cn.xuqiudong.mq.bridge.model;

import cn.xuqiudong.mq.bridge.vo.AbstractDataBridgeVo;

/**
 * 描述:
 *      因为发送到mq的消息需要包含一些元数据，所以这里对原始消息内容进行包装
 * @author Vic.xu
 * @since 2025-03-04 16:01
 */
public class MessageContentWrapper<T extends AbstractDataBridgeVo> {

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 模块名称
     */
    private String module;
    /**
     * 动作名称
     */
    private String action;

    /**
     * 发到哪个queue
     */
    private String queueName;

    /**
     * 发送都标识
     */
    private String flag;
    /**
     * 消息内容
     */
    private T message;

    public MessageContentWrapper() {
    }

    public MessageContentWrapper(DataBridgeSendMessage meta, T message) {
        this.messageId = meta.getMessageId();
        this.module = meta.getModule();
        this.action = meta.getAction();
        this.queueName = meta.getQueueName();
        this.message = message;
        this.flag = meta.getFlag();
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
