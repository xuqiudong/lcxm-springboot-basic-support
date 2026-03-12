package cn.xuqiudong.mq.bridge.core;

import cn.xuqiudong.basic.core.vo.BooleanWithMsg;
import cn.xuqiudong.basic.core.util.JsonUtil;
import cn.xuqiudong.mq.bridge.model.ActionHandlerModel;
import cn.xuqiudong.mq.bridge.model.DataBridgeReceiveMessage;
import cn.xuqiudong.mq.bridge.vo.AbstractDataBridgeVo;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 消费消费的路由入口： 持有所有的消费者处理器
 *
 * @author Vic.xu
 * @since 2025-03-07 13:41
 */
@Component
public class DataBridgeMessageRouter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeMessageRouter.class);


    private List<AbstractDataBridgeMessageConsumer> consumers;

    /**
     * key : module
     * value :
     * map: key = action, value = ActionHandlerModel(持有自身的Bean ,即AbstractDataBridgeConsumer 子类 和 操作的method)
     */
    private Map<String, Map<String, ActionHandlerModel>> consumerMap;

    @Autowired(required = false)
    public void setConsumers(List<AbstractDataBridgeMessageConsumer> consumers) {
        this.consumers = consumers;
    }


    @PostConstruct
    private void init() {
        consumerMap = new HashMap<>();
        if (consumers == null) {
            LOGGER.info("当前环境没有注册消费消息的处理器");
            return;
        }
        int actionSize = 0;
        // 注册 消费者和其 action 的映射关系
        for (AbstractDataBridgeMessageConsumer consumer : consumers) {
            String module = consumer.module();
            Map<String, ActionHandlerModel> actionHandlerModelMap = consumer.registerHandlers(consumer);
            // 防止一个module 写了多个实现类
            Map<String, ActionHandlerModel> moduleMap = consumerMap.get(module);
            if (moduleMap == null) {
                moduleMap = new HashMap<>();
                consumerMap.put(module, moduleMap);
            }
            actionSize += actionHandlerModelMap.size();
            moduleMap.putAll(actionHandlerModelMap);
        }
        LOGGER.info("初始化完成，共注册 {} 个消息处理器模块, {}个消息处理方法", consumerMap.size(), actionSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public BooleanWithMsg dispatchMessage(@NotNull DataBridgeReceiveMessage message) throws Throwable {
        ActionHandlerModel actionHandler = findActionHandler(message.getModule(), message.getAction());
        if (actionHandler == null) {
            return BooleanWithMsg.fail("没有找到对应的消息处理器");
        }
        try {
            long start = System.currentTimeMillis();
            AbstractDataBridgeVo abstractDataBridgeVo = JsonUtil.jsonToObject(message.getMessage(), actionHandler.getFlag().messageType());
            BooleanWithMsg result = actionHandler.invoke(abstractDataBridgeVo);
            long end = System.currentTimeMillis();
            LOGGER.info("消费[{}]消息[{}]用时[{}]ms", message.getModule() + "-" + message.getAction(),
                    message.getMessageId(), (end - start));
            return result;
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                InvocationTargetException exception = (InvocationTargetException) e;
                throw exception.getTargetException();
            }
            throw e;

        }

    }

    /**
     * 查找到一条消息的处理类
     */
    private ActionHandlerModel findActionHandler(String module, String action) {
        Map<String, ActionHandlerModel> moduleMap = consumerMap.get(module);
        if (moduleMap == null) {
            throw new RuntimeException("没有找到模块[" + module + "." + action+"]对应的消息处理器");
        }
        return moduleMap.get(action);
    }


}
