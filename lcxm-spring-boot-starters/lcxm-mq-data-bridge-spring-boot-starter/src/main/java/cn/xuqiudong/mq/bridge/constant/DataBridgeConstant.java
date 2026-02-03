package cn.xuqiudong.mq.bridge.constant;

/**
 * 描述:
 * 一些常量
 *
 * @author Vic.xu
 * @since 2025-02-27 9:50
 */
public final class DataBridgeConstant {

    /**
     * 是否启用mq： 关闭的时候停止消费和发送
     */
    public static final boolean DEFAULT_MQ_ENABLE = true;

    /**
     * 是否启用发送： 关闭的时候不发送消息
     */
    public static final boolean DEFAULT_SEND_ENABLE = true;

    /**
     * 是否启用接收： 关闭的时候不接收消息
     */
    public static final boolean DEFAULT_RECEIVE_ENABLE= true;

    /**
     * 是否启用本地消费： 关闭的时候不消费消息
     */
    public static final boolean DEFAULT_CONSUME_ENABLE = true;

    /**
     * 默认的交换机名称
     */
    public static final String DEFAULT_EXCHANGE = "data_bridge_exchange";

    /**
     * 默认发送消息的队列: 理应配置
     */
    public static final String DEFAULT_SEND_QUEUE = "data_bridge_send_queue";

    /**
     * 默认发送消息的路由键: 理应配置
     */
    public static final String DEFAULT_SEND_ROUTING_KEY = "data_bridge_send_routing_key";

    /**
     * 默认发送消息的confirm超时时间 毫秒: 理应配置
     */
    public static final long DEFAULT_SEND_CONFIRM_TIMEOUT = 5000;

    /**
     * 默认接收消息的队列: 理应配置
     */
    public static final String DEFAULT_RECEIVE_QUEUE = "data_bridge_receive_queue";

    /**
     *  默认接受消息的路由键: 理应配置
     */
    public static final String DEFAULT_RECEIVE_ROUTING_KEY = "data_bridge_receive_routing_key";

    public static final String REDIS_KEY_PREFIX = "data:bridge:";

    /**
     * mq是否启用的 redis key
     */
    public static final String REDIS_KEY_MQ_ENABLE = REDIS_KEY_PREFIX + "mq:enable";

    /**
     * 发送是否启用的 redis key
     */
    public static final String REDIS_KEY_SEND_ENABLE = REDIS_KEY_PREFIX + "send:enable";

    /**
     * 接收是否启用的 redis key
     */
    public static final String REDIS_KEY_RECEIVE_ENABLE = REDIS_KEY_PREFIX + "receive:enable";

    /**
     * 本地消费是否启用的 redis key
     */
    public static final String REDIS_KEY_CONSUMER_ENABLE = REDIS_KEY_PREFIX + "consumer:enable";


    /**
     * 发送是否正在运行的 redis key
     */
    public static final String REDIS_KEY_SEND_RUNNING = REDIS_KEY_PREFIX+"send:running";

    /**
     * 消费消息是否正在运行的 redis key
     */
    public static final String REDIS_KEY_CONSUMER_RUNNING = REDIS_KEY_PREFIX+"receive:running";

    /**
     * 重试初始延迟时间 毫秒
     */
    public  static final long INITIAL_RETRY_DELAY = 2000;

    /**
     * 重试最大延迟时间 毫秒
     */
    public  static final long MAX_RETRY_DELAY = 32000;

    /**
     * 每次提取多少消息发送
     */
    public static final int SEND_MESSAGE_FETCH_SIZE = 200;

    /**
     * 每次提取多少消息消费
     */
    public static final int CONSUMER_MESSAGE_FETCH_SIZE = 200;

}
