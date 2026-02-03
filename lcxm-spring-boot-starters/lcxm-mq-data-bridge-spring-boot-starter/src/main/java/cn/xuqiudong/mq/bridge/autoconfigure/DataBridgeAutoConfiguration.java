package cn.xuqiudong.mq.bridge.autoconfigure;

import cn.xuqiudong.mq.bridge.mq.DataBridgeMqMessageReceiver;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 描述:
 * 基于RabbitMQ的系统间数据顺序交互starter 自动装配入口
 * 当  data.bridge.enabled = false 的时候 不启用
 *
 * @author Vic.xu
 * @since 2025-02-26 13:39
 */
@Configuration
@ConditionalOnProperty(prefix = "data.bridge", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass({RabbitTemplate.class, ConnectionFactory.class, JdbcTemplate.class, RedisTemplate.class})
@EnableConfigurationProperties(DataBridgeProperties.class)
// 添加mybatis的mapper扫描路径
@MapperScan("cn.xuqiudong.mq.bridge.mapper")
// 扫描注册一些helper、service、controller ,mq发送接收工具类等
@ComponentScan("cn.xuqiudong.mq.bridge")
@EnableAsync
public class DataBridgeAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeAutoConfiguration.class);

    private final DataBridgeProperties dataBridgeProperties;

    public DataBridgeAutoConfiguration(DataBridgeProperties dataBridgeProperties) {
        this.dataBridgeProperties = dataBridgeProperties;
    }

    /**
     * 如果用户没有提供自定义的 TaskExecutor，则自动提供默认的
     * 因为用户完成插入消息后需要异步发送消息
     */
    @Bean
    @ConditionalOnMissingBean(TaskExecutor.class)
    public TaskExecutor defaultAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.initialize();
        return executor;
    }

    /**
     * 创建并配置 RabbitMQ 消息监听容器
     * - 监听指定的队列
     * - 仅允许单个消费者，确保顺序消费
     * - 仅预取 1 条消息，避免多个消息并行处理
     * - 采用手动确认模式，确保消息处理成功后才确认
     */
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory,
                                                                   DataBridgeMqMessageReceiver dataBridgeReceiver) {
        LOGGER.info("定义消息监听容器(监听队列 [{}]): 方便动态启停, startup status = {}",
                dataBridgeProperties.getReceiveQueue(), dataBridgeProperties.isReceiveEnabled());
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(dataBridgeProperties.getReceiveQueue());
        // 不再使用  MessageListenerAdapter， 而是直接使用 DataBridgeReceiver 实现 ChannelAwareMessageListener
        // 因为这边会使用channel手动确认
        container.setMessageListener(dataBridgeReceiver);
        container.setAutoStartup(dataBridgeProperties.isReceiveEnabled());
        // 只允许单个消费者，确保严格顺序消费
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        // 仅预取 1 条消息，确保消费完成后再拉取新消息
        container.setPrefetchCount(1);
        // 采用手动确认，确保业务逻辑成功后才确认消息
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(dataBridgeProperties.getExchange());
    }

    @Bean
    public Queue sendQueue() {
        return new Queue(dataBridgeProperties.getSendQueue(), true);
    }

    @Bean
    public Queue receiveQueue() {  // ✅ 修正拼写
        return new Queue(dataBridgeProperties.getReceiveQueue(), true);
    }

    @Bean
    public Binding bindingSendQueue() {
        return BindingBuilder.bind(sendQueue()).to(exchange()).with(dataBridgeProperties.getSendRoutingKey());
    }

    @Bean
    public Binding bindingReceiveQueue() {
        return BindingBuilder.bind(receiveQueue()).to(exchange()).with(dataBridgeProperties.getReceiveRoutingKey());
    }
}
