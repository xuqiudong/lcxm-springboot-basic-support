package cn.xuqiudong.mq.bridge.facade;

import cn.xuqiudong.common.base.vo.BooleanWithMsg;
import cn.xuqiudong.mq.bridge.autoconfigure.DataBridgeProperties;
import cn.xuqiudong.mq.bridge.constant.DataBridgeConstant;
import cn.xuqiudong.mq.bridge.enums.OperationEnum;
import cn.xuqiudong.mq.bridge.enums.SendStatusEnum;
import cn.xuqiudong.mq.bridge.helper.ClusterOperationStateManagerHelper;
import cn.xuqiudong.mq.bridge.helper.DataBridgeGlobalConfigHelper;
import cn.xuqiudong.mq.bridge.model.DataBridgeSendMessage;
import cn.xuqiudong.mq.bridge.mq.DataBridgeMqMessageSender;
import cn.xuqiudong.mq.bridge.service.DataBridgeSendMessageService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 发送消息的门面:
 * 1. 系统启动就进行待发送消息查找，并开始发送消息
 * 2. 手动触发
 *
 * @author Vic.xu
 * @since 2025-03-04 10:37
 */
@Component
public class DataBridgeMessageSenderFacade extends AbstractDataBridgeMessageFacade implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeMessageSenderFacade.class);

    private final DataBridgeSendMessageService dataBridgeSendMessageService;
    private final DataBridgeMqMessageSender dataBridgeSender;
    private final DataBridgeProperties dataBridgeProperties;

    private ApplicationContext applicationContext;

    public DataBridgeMessageSenderFacade(DataBridgeGlobalConfigHelper dataBridgeGlobalSwitchHelper,
                                         DataBridgeSendMessageService dataBridgeSendMessageService,
                                         DataBridgeMqMessageSender dataMessageSender,
                                         DataBridgeProperties dataBridgeProperties,
                                         ClusterOperationStateManagerHelper clusterOperationStateManagerHelper) {
        super(dataBridgeGlobalSwitchHelper, clusterOperationStateManagerHelper);
        this.dataBridgeSendMessageService = dataBridgeSendMessageService;
        this.dataBridgeSender = dataMessageSender;
        this.dataBridgeProperties = dataBridgeProperties;
    }

    /**
     * 保证在当前事务提交后 执行异步消息发送
     */
    public void startSendAsyncWithTransactionCheck() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // 事务提交后再执行 startSendAsync()
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    startSendAsync();
                }
            });
        } else {
            // 没有事务，直接执行
            startSendAsync();
        }
    }

    /**
     * 异步消息发送
     * 这个方法不会阻塞主业务
     */
    @Async
    public void startSendAsync() {
        LOGGER.info("异步触发消息发送...");
        startSend();
    }

    /**
     * 启动消息发送
     * 1. 每次生成消息入库的时候 触发此方法
     * 2. 人工处理后，手动触发本方法
     * <p>
     * 采用 while 循环避免递归调用导致的 StackOverflowError
     */
    public void startSend() {
        OperationEnum operation = OperationEnum.SEND;
        String switchKey = DataBridgeConstant.REDIS_KEY_SEND_ENABLE;
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
                List<DataBridgeSendMessage> dataBridgeSendMessages =
                        dataBridgeSendMessageService.fetchMessageToSend(lastTimeId);

                LOGGER.info("进入消息发送, size = {}", dataBridgeSendMessages.size());

                if (CollectionUtils.isEmpty(dataBridgeSendMessages)) {
                    // 没有消息可发送，退出循环
                    break;
                }
                DataBridgeMessageSenderFacade self = applicationContext.getBean(DataBridgeMessageSenderFacade.class);
                for (DataBridgeSendMessage dataBridgeSendMessage : dataBridgeSendMessages) {
                    lastTimeId = dataBridgeSendMessage.getId();
                     BooleanWithMsg result;
                    try {
                        result = self.send(dataBridgeSendMessage);
                    } catch (Exception e) {
                        LOGGER.error("消息发送失败", e);
                        result = BooleanWithMsg.fail("消息发送失败:" + e.getMessage());
                    }
                    if (!result.isSuccess()) {
                        LOGGER.error("消息发送失败:{}, 关闭消息发送", result.getMessage());
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


    /**
     * 根据id发送消息
     *
     * @param id DataBridgeSendMessage 的id
     */
    @Transactional(rollbackFor = Exception.class)
    public BooleanWithMsg send(Integer id) {
        if (!dataBridgeGlobalSwitchHelper.isSendEnable()) {
            return BooleanWithMsg.fail("发送消息功能已关闭");
        }
        DataBridgeSendMessage entity = dataBridgeSendMessageService.findById(id);
        if (entity == null) {
            return BooleanWithMsg.fail("消息[" + id + "]不存在");
        }
        return send(entity);
    }

    /**
     * 发送消息
     *
     * @param entity 待发送消息实体
     * @return 发送结果
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BooleanWithMsg send(DataBridgeSendMessage entity) {
        if (entity == null) {
            return BooleanWithMsg.fail("消息不存在");
        }
        if (!dataBridgeGlobalSwitchHelper.isSendEnable()) {
            return BooleanWithMsg.fail("发送消息功能已关闭");
        }
        Integer id = entity.getId();
        if (entity.isDelete()) {
            return BooleanWithMsg.fail("消息[" + id + "]已删除");
        }
        if (!entity.isEnable()) {
            return BooleanWithMsg.fail("消息[" + id + "]已禁用");
        }
        // 判断状态：只有未发送和 已修正的数据才能发送 （其实数据库层面已经处理过）
        if (SendStatusEnum.INITIAL != entity.getStatus() && SendStatusEnum.AMENDED != entity.getStatus()) {
            return BooleanWithMsg.fail("消息[" + id + "]状态为[" + entity.getStatus().getDesc() + "]，不能发送");
        }
        // 发送消息并进行指数退避重试
        BooleanWithMsg result = dataBridgeSender.sendWithRetry(entity.getMessageId(), entity.getMessage(), dataBridgeProperties.getSendRoutingKey());
        afterSend(entity, result);
        return result;
    }

    /**
     * 发送后处理: 更新发送状态，如果失败，将阻塞发送
     */
    public void afterSend(DataBridgeSendMessage entity, BooleanWithMsg result) {
        boolean success = result.isSuccess();
        if (success) {
            entity.setStatus(SendStatusEnum.SUCCESS);
            entity.setNote("");
        } else {
            entity.setStatus(SendStatusEnum.FAILED);
            entity.setNote(StringUtils.abbreviate(result.getMessage(), 512));
            //阻塞发送
            LOGGER.warn("消息[{}]发送失败，将要阻塞全局消息发送!!!!!!!", entity.getId());
            dataBridgeGlobalSwitchHelper.setSendEnable(false);
        }
        dataBridgeSendMessageService.save(entity);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

