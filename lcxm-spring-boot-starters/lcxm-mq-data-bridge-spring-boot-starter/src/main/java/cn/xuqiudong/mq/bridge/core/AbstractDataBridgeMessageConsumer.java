package cn.xuqiudong.mq.bridge.core;

import cn.xuqiudong.basic.core.vo.BooleanWithMsg;
import cn.xuqiudong.mq.bridge.annotation.ActionHandler;
import cn.xuqiudong.mq.bridge.model.ActionHandlerModel;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 *  消费消息的基类，继承该类
 *
 * @author Vic.xu
 * @since 2025-03-07 10:58
 */
public abstract class AbstractDataBridgeMessageConsumer {

    /**
     * 业务模块
     */
    public abstract String module();

    /**
     * 注册各个action
     * @param selfBean 当前类，被spring管理的bean
     */
    public Map<String, ActionHandlerModel> registerHandlers(AbstractDataBridgeMessageConsumer selfBean) {
        Map<String, ActionHandlerModel> result = new HashMap<>();
        Class<?> clazz = this.getClass();
        // 只处理 public 方法
        for (Method method : clazz.getDeclaredMethods()) {
            //1. 只处理public方法
            //2. 必须被标记为 ActionHandler
            boolean isPublic = Modifier.isPublic(method.getModifiers());
            ActionHandler actionHandler = method.getAnnotation(ActionHandler.class);
            if (!isPublic || actionHandler == null) {
                continue;
            }

            //3. 返回值为 BooleanWithMsg
            //4. 方法参数必须为 ActionHandler 中定义的 ActionHandler.messageType()
            boolean isValid = (BooleanWithMsg.class == method.getReturnType())
                    && (method.getParameterCount() == 1)
                    && (method.getParameterTypes()[0] == actionHandler.messageType());
            if (!isValid) {
                continue;
            }
            result.put(actionHandler.action(), new ActionHandlerModel<>(actionHandler, selfBean, method));

        }
        return result;
    }

}
