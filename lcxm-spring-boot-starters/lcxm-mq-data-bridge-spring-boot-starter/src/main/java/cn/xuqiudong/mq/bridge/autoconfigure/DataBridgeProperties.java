package cn.xuqiudong.mq.bridge.autoconfigure;

import cn.xuqiudong.mq.bridge.constant.DataBridgeConstant;
import cn.xuqiudong.mq.bridge.enums.ArchivePeriodEnum;
import lombok.Data;
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
@Data
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

    /**
     * 每次提取多少消息发送
     * 默认200
     * 配置项 data.bridge.send-message-fetch-size
     */
    private int sendMessageFetchSize = DataBridgeConstant.SEND_MESSAGE_FETCH_SIZE;

    /**
     * 每次提取多少消息消费
     * 默认200
     * 配置项 data.bridge.consumer-message-fetch-size
     */
    private int consumerMessageFetchSize = DataBridgeConstant.CONSUMER_MESSAGE_FETCH_SIZE;

    /**
     * 归档方式： 默认按年
     * 配置项 data.bridge.archive-type=
     */
    private ArchivePeriodEnum archiveType = ArchivePeriodEnum.YEAR;

    /**
     * 是否归档
     * 配置项 data.bridge.archive-enabled=
     */
    private boolean archiveEnabled = true;
}