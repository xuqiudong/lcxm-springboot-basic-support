package cn.xuqiudong.mq.bridge.model;

import cn.xuqiudong.basic.core.model.BaseGenericEntity;
import cn.xuqiudong.mq.bridge.enums.SendStatusEnum;

/**
 * 发送到mq的消息表 实体类
 *
 * @author Vic.xu
 */
public class DataBridgeSendMessage extends BaseGenericEntity<Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * 消息 ID（唯一标识）
     */
    private String messageId;
    /**
     * 队列名称
     */
    private String queueName;
    /**
     * 模块名称
     */
    private String module;
    /**
     * 动作名称
     */
    private String action;
    /**
     * 消息内容
     */
    private String message;
    /**
     * 状态（待发送、已发送、发送失败 已丢弃）
     */
    private SendStatusEnum status;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 备注说明
     */
    private String note;

    /**
     * 标识
     */
    private String flag;

    /***************** set|get start **************************************/

    /**
     * set：消息 ID（唯一标识）
     */
    public DataBridgeSendMessage setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    /**
     * get：消息 ID（唯一标识）
     */

    public String getMessageId() {
        return messageId;
    }

    /**
     * set：队列名称
     */
    public DataBridgeSendMessage setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    /**
     * get：队列名称
     */

    public String getQueueName() {
        return queueName;
    }

    /**
     * set：模块名称
     */
    public DataBridgeSendMessage setModule(String module) {
        this.module = module;
        return this;
    }

    /**
     * get：模块名称
     */

    public String getModule() {
        return module;
    }

    /**
     * set：动作名称
     */
    public DataBridgeSendMessage setAction(String action) {
        this.action = action;
        return this;
    }

    /**
     * get：动作名称
     */

    public String getAction() {
        return action;
    }

    /**
     * set：消息内容
     */
    public DataBridgeSendMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * get：消息内容
     */

    public String getMessage() {
        return message;
    }

    /**
     * set：状态（待发送、已发送、发送失败）
     */
    public DataBridgeSendMessage setStatus(SendStatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * get：状态（待发送、已发送、发送失败）
     */

    public SendStatusEnum getStatus() {
        return status;
    }

    /**
     * set：重试次数
     */
    public DataBridgeSendMessage setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * get：重试次数
     */

    public Integer getRetryCount() {
        return retryCount;
    }

    /**
     * set：备注说明
     */
    public DataBridgeSendMessage setNote(String note) {
        this.note = note;
        return this;
    }

    /**
     * get：备注说明
     */

    public String getNote() {
        return note;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    /***************** set|get end **************************************/
}
