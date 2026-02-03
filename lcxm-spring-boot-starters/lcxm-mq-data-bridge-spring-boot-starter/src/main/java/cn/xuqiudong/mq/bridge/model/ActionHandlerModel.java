package cn.xuqiudong.mq.bridge.model;

import cn.xuqiudong.common.base.vo.BooleanWithMsg;
import cn.xuqiudong.mq.bridge.annotation.ActionHandler;
import cn.xuqiudong.mq.bridge.core.AbstractDataBridgeMessageConsumer;
import cn.xuqiudong.mq.bridge.vo.AbstractDataBridgeVo;

import java.lang.reflect.Method;

/**
 * 描述:
 *      标注 消息消费的的实例和其对应的method
 * @author Vic.xu
 * @since 2025-03-07 11:26
 */
public class ActionHandlerModel<T extends AbstractDataBridgeMessageConsumer> {

    private ActionHandler flag;

    private T instance;

    /**
     * 被 TaskJobFlag 标注的方法  必须为 public
     */
    private Method method;

    public ActionHandlerModel(ActionHandler flag, T instance, Method method) {
        this.flag = flag;
        this.instance = instance;
        this.method = method;
    }

    public ActionHandler getFlag() {
        return flag;
    }

    public void setFlag(ActionHandler flag) {
        this.flag = flag;
    }

    public T getInstance() {
        return instance;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public BooleanWithMsg invoke(AbstractDataBridgeVo vo) throws Exception {
        return (BooleanWithMsg) method.invoke(instance, vo);
    }

}
