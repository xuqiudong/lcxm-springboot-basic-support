package cn.xuqiudong.mq.bridge.facade;

import cn.xuqiudong.common.base.vo.BooleanWithMsg;
import cn.xuqiudong.common.util.JsonUtil;
import cn.xuqiudong.mq.bridge.constant.DataBridgeConstant;
import cn.xuqiudong.mq.bridge.core.DataBridgeMessageRouter;
import cn.xuqiudong.mq.bridge.enums.OperationEnum;
import cn.xuqiudong.mq.bridge.enums.ReceiveStatusEnum;
import cn.xuqiudong.mq.bridge.helper.ClusterOperationStateManagerHelper;
import cn.xuqiudong.mq.bridge.helper.DataBridgeGlobalConfigHelper;
import cn.xuqiudong.mq.bridge.model.DataBridgeReceiveMessage;
import cn.xuqiudong.mq.bridge.model.MessageContentWrapper;
import cn.xuqiudong.mq.bridge.mq.DataBridgeMqMessageReceiver;
import cn.xuqiudong.mq.bridge.service.DataBridgeReceiveMessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 对接收到的消息进行处理和分发
 * 对上承接 消息监听器 DataBridgeReceiver  对消息进入入库操作
 * <p>
 * 对下 进行消息分发，找到消息的真正处理类
 *
 * @author Vic.xu
 * @see DataBridgeMqMessageReceiver
 * @since 2025-03-07 9:13
 */
@Component
public class DataBridgeMessageReceiverFacade extends AbstractDataBridgeMessageFacade implements ApplicationContextAware {

    private final DataBridgeReceiveMessageService dataBridgeReceiveMessageService;

    private final DataBridgeMessageRouter dataBridgeMessageDispatcher;

    private ApplicationContext applicationContext;

    private DataBridgeMessageReceiverFacade self;

    public DataBridgeMessageReceiverFacade(DataBridgeReceiveMessageService dataBridgeReceiveMessageService,
                                           DataBridgeGlobalConfigHelper dataBridgeGlobalSwitchHelper,
                                           DataBridgeMessageRouter dataBridgeMessageDispatcher,
                                           ClusterOperationStateManagerHelper clusterOperationStateManagerHelper) {
        super(dataBridgeGlobalSwitchHelper, clusterOperationStateManagerHelper);
        this.dataBridgeReceiveMessageService = dataBridgeReceiveMessageService;
        this.dataBridgeMessageDispatcher = dataBridgeMessageDispatcher;
    }

    private DataBridgeMessageReceiverFacade getSelf() {
        if (self == null) {
            self = applicationContext.getBean(DataBridgeMessageReceiverFacade.class);
        }
        return self;
    }

    /**
     * 处理消息，进行入库操作
     * 1. 解析正常的消息，进行入库操作
     * 2. 如果解析失败，则原样入库，并设置状态为解析失败
     * 3.  触发消息的异步消费
     *
     * @param messageBody 消息体 参见 MessageContentWrapper
     * @see MessageContentWrapper
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void handle(String msgId, @NotNull String messageBody) {
        DataBridgeReceiveMessage receiveMessage;
        try {
            // 将 messageBody 解析为 JsonNode
            ObjectMapper objectMapper = JsonUtil.OBJECT_MAPPER;
            JsonNode rootNode = objectMapper.readTree(messageBody);

            // 提取 message 字段的 JSON 数据
            JsonNode messageNode = rootNode.get("message");
            String messageJson = messageNode != null ? messageNode.toString() : null;

            // 解析 MessageContentWrapper（不包含 message 字段的具体类型）
            MessageContentWrapper<?> messageContentWrapper = objectMapper.treeToValue(rootNode, MessageContentWrapper.class);

            // 将 message 字段的 JSON 数据设置到 receiveMessage
            receiveMessage = new DataBridgeReceiveMessage(messageContentWrapper, messageJson);
        } catch (Exception e) {
            //解析失败，原样入库,其他字段不设置
            LOGGER.error("handle message error, messageBody: {}", messageBody, e);
            receiveMessage = new DataBridgeReceiveMessage();
            receiveMessage.setMessageId(msgId);
            receiveMessage.setStatus(ReceiveStatusEnum.PARSE_ERROR);
            receiveMessage.setMessage(messageBody);
            receiveMessage.setNote(ReceiveStatusEnum.PARSE_ERROR.getText());
        }
        // 插入接收到的消息入库
        int count = dataBridgeReceiveMessageService.checkMessageIdThenInsert(receiveMessage);
        if (count > 0) {
            // 触发消息的异步消费
            startConsumerAsyncWithTransactionCheck();
        }
    }

    /**
     * 保证在当前事务提交后 执行异步消息发送
     */
    public void startConsumerAsyncWithTransactionCheck() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // 事务提交后再执行 startSendAsync()
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    getSelf().startConsumerAsync();
                }
            });
        } else {
            // 没有事务，直接执行
            getSelf().startConsumerAsync();
        }
    }

    /**
     * 异步消费发送
     * 这个方法不会阻塞主业务
     */
    @Async
    public void startConsumerAsync() {
        LOGGER.info("异步触发消息消费...");
        startConsumer();
    }

    /**
     * 启动消息消费： 消费本地数据库消息 不受mq开关限制,但是收到本地 消费是否阻塞的限制
     * 1. 每次mq消息入库的时候 触发此方法
     * 2. 人工处理后，手动触发本方法
     * <p>
     * 采用 while 循环避免递归调用导致的 StackOverflowError
     */
    public void startConsumer() {
        OperationEnum operation = OperationEnum.CONSUME;
        String switchKey = DataBridgeConstant.REDIS_KEY_CONSUMER_ENABLE;
        RLock lock = beforeOperation(operation, switchKey, 15);
        if (lock == null) {
            return;
        }
        try {
            // 设置本地状态
            stateManager.updateLocalState(operation, true);
            Integer lastTimeId = null;
            // 此处使用while 而不是递归是为了防止，数据不断的情况下 可能出现 栈内存溢出的情况
            while (true) {
                List<DataBridgeReceiveMessage> dataBridgeReceiveMessages =
                        dataBridgeReceiveMessageService.fetchMessageToConsumer(lastTimeId);

                LOGGER.info("进入本地消息消费, size = {}", dataBridgeReceiveMessages.size());

                if (CollectionUtils.isEmpty(dataBridgeReceiveMessages)) {
                    // 没有消息可处理，退出循环
                    break;
                }
                DataBridgeMessageReceiverFacade self = getSelf();

                for (DataBridgeReceiveMessage message : dataBridgeReceiveMessages) {
                    lastTimeId = message.getId();
                    BooleanWithMsg result;
                    try {
                        // 开启新事务
                        result = self.consumer(message);
                    } catch (Exception e) {
                        result = BooleanWithMsg.fail("消息消费失败:" + e.getMessage());
                    }
                    if (!result.isSuccess()) {
                        LOGGER.error("消息消费失败:{}, 关闭消息消费", result.getMessage());
                        return;
                    }
                }

                try {
                    // 休眠1s后继续下一轮发送
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    // 处理中断异常，恢复线程的中断状态
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            // 重置状态
            afterOperation(operation, lock);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BooleanWithMsg consumer(DataBridgeReceiveMessage entity) {
        //再次判断是否 可以消费
        if (!dataBridgeGlobalSwitchHelper.isConsumerEnable()) {
            return BooleanWithMsg.fail("消费消息功能已关闭");
        }
        // 判断状态：只有未发送和 已修正的数据才能发送 （其实数据库层面已经处理过）
        ReceiveStatusEnum status = entity.getStatus();
        if (status == ReceiveStatusEnum.PARSE_ERROR) {
            LOGGER.warn("消息[{}]状态为[{}]，无法消费, 需要人工处理后方可消费", entity.getId(), entity.getStatus().getText());
            dataBridgeGlobalSwitchHelper.setConsumerEnable(false);
            // FIXME  是否需要通知人工处理？
            return BooleanWithMsg.fail("消息[" + entity.getId() + "]状态为[" + entity.getStatus().getText() + "]，不能消费");
        }
        // 查询的时候已经过滤 可忽略掉
        if (ReceiveStatusEnum.INITIAL != status && ReceiveStatusEnum.AMENDED != status) {
            return BooleanWithMsg.fail("消息[" + entity.getId() + "]状态为[" + entity.getStatus().getText() + "]，不能消费");
        }
        BooleanWithMsg result;
        try {
            result = dataBridgeMessageDispatcher.dispatchMessage(entity);
        } catch (Throwable e) {
            result = BooleanWithMsg.fail("消息消费失败:" + e.getMessage());
            LOGGER.error("消息消费失败", e);
        }
        //保证在新的事务中运行
        getSelf().afterConsumer(entity, result);
        return result;
    }

    /**
     * 发送后处理: 更新发送状态，如果失败，将阻塞发送
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void afterConsumer(DataBridgeReceiveMessage entity, BooleanWithMsg result) {
        boolean success = result.isSuccess();
        if (success) {
            entity.setStatus(ReceiveStatusEnum.SUCCESS);
            entity.setNote("");
        } else {
            entity.setStatus(ReceiveStatusEnum.FAILED);
            entity.setNote(StringUtils.abbreviate(result.getMessage(), 512));
            //阻塞发送
            LOGGER.warn("消息[{}]消费失败，将要阻塞全局消息消费!!!!!!!", entity.getId());
            dataBridgeGlobalSwitchHelper.setConsumerEnable(false);
        }
        dataBridgeReceiveMessageService.save(entity);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
