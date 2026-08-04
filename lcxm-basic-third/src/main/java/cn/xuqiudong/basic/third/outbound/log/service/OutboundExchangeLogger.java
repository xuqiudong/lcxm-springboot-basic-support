package cn.xuqiudong.basic.third.outbound.log.service;

import cn.xuqiudong.basic.third.outbound.log.model.OutboundExchangeLog;

/**
 * 出站交换日志自定义处理接口。
 *
 * <p>项目可实现该接口完成入库、MQ、ES、审计等处理。</p>
 *
 * @author Vic.xu
 */
public interface OutboundExchangeLogger {

    /**
     * 处理一次出站请求交换日志。
     */
    void log(OutboundExchangeLog log);
}
