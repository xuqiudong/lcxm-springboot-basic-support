package cn.xuqiudong.basic.third.inbound.log.service;

import cn.xuqiudong.basic.third.inbound.log.model.InboundInvokeLog;

/**
 * 第三方入站调用日志落点扩展。
 *
 * <p>项目可实现该接口完成入库、MQ、ES 等处理。</p>
 *
 * @author Vic.xu
 */
public interface InboundInvokeLogger {

    /**
     * 记录一次入站业务方法调用。
     */
    void log(InboundInvokeLog log);
}
