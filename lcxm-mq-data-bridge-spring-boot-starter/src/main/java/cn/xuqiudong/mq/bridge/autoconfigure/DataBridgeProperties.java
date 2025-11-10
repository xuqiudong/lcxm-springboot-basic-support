package cn.xuqiudong.mq.bridge.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_CONSUME_ENABLE;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_EXCHANGE;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_MQ_ENABLE;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_RECEIVE_ENABLE;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_RECEIVE_QUEUE;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_RECEIVE_ROUTING_KEY;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_SEND_CONFIRM_TIMEOUT;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_SEND_ENABLE;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_SEND_QUEUE;
import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.DEFAULT_SEND_ROUTING_KEY;


@ConfigurationProperties(prefix = "data.bridge")
public class DataBridgeProperties {

    /**
     * 是否启用MQ
     */
    private boolean mqEnabled = DEFAULT_MQ_ENABLE;

    /**
     * 是否启用发送功能
     */
    private boolean sendEnabled = DEFAULT_SEND_ENABLE;
    /**
     * 是否启用接收功能
     */
    private boolean receiveEnabled = DEFAULT_RECEIVE_ENABLE;

    /**
     * 是否启用消费功能
     */
    private boolean consumeEnabled = DEFAULT_CONSUME_ENABLE;

    /**
     * 交换机
     */
    private String exchange = DEFAULT_EXCHANGE;
    /**
     * 发送消息的队列
     */
    private String sendQueue = DEFAULT_SEND_QUEUE;

    /**
     * 发送消息的路由键
     */
    private String sendRoutingKey = DEFAULT_SEND_ROUTING_KEY;

    /**
     * 送消息的confirm超时时间 毫秒
     */
    private long sendConfirmTimeout = DEFAULT_SEND_CONFIRM_TIMEOUT;
    /**
     * 接收消息的队列
     */
    private String receiveQueue = DEFAULT_RECEIVE_QUEUE;

    /**
     * 接收消息的队列路由键
     */
    private String receiveRoutingKey = DEFAULT_RECEIVE_ROUTING_KEY;

    /**
     * 重试次数
     */
    private int retryCount = 3;

    /**
     * 生成的消息的标识： 入库消息携带此标识
     */
    private String produceFlag;

    /**
     * 发送时的标识，理应只在开发环境使用
     */
    private String sendFlag;

    /**
     * 消费时的标识，理应只在开发环境使用
     */
    private String consumeFlag;

    public boolean isMqEnabled() {
        return mqEnabled;
    }

    public void setMqEnabled(boolean mqEnabled) {
        this.mqEnabled = mqEnabled;
    }

    public boolean isSendEnabled() {
        return sendEnabled;
    }

    public void setSendEnabled(boolean sendEnabled) {
        this.sendEnabled = sendEnabled;
    }

    public boolean isReceiveEnabled() {
        return receiveEnabled;
    }

    public void setReceiveEnabled(boolean receiveEnabled) {
        this.receiveEnabled = receiveEnabled;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSendQueue() {
        return sendQueue;
    }

    public void setSendQueue(String sendQueue) {
        this.sendQueue = sendQueue;
    }

    public String getReceiveQueue() {
        return receiveQueue;
    }

    public void setReceiveQueue(String receiveQueue) {
        this.receiveQueue = receiveQueue;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getSendRoutingKey() {
        return sendRoutingKey;
    }

    public void setSendRoutingKey(String sendRoutingKey) {
        this.sendRoutingKey = sendRoutingKey;
    }

    public String getReceiveRoutingKey() {
        return receiveRoutingKey;
    }

    public void setReceiveRoutingKey(String receiveRoutingKey) {
        this.receiveRoutingKey = receiveRoutingKey;
    }

    public long getSendConfirmTimeout() {
        return sendConfirmTimeout;
    }

    public void setSendConfirmTimeout(long sendConfirmTimeout) {
        this.sendConfirmTimeout = sendConfirmTimeout;
    }

    public boolean isConsumeEnabled() {
        return consumeEnabled;
    }

    public String getProduceFlag() {
        return produceFlag;
    }

    public void setProduceFlag(String produceFlag) {
        this.produceFlag = produceFlag;
    }

    public void setConsumeEnabled(boolean consumeEnabled) {
        this.consumeEnabled = consumeEnabled;
    }

    public String getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(String sendFlag) {
        this.sendFlag = sendFlag;
    }

    public String getConsumeFlag() {
        return consumeFlag;
    }

    public void setConsumeFlag(String consumeFlag) {
        this.consumeFlag = consumeFlag;
    }
}