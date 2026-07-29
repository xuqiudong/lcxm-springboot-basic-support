package cn.xuqiudong.basic.third.log.service;

import cn.xuqiudong.basic.third.log.model.ThirdExchangeLog;

/**
 * 出站交换日志处理接口。
 *
 * @author Vic.xu
 */
public interface ThirdExchangeLogger {

    /**
     * 处理一次出站请求交换日志。
     */
    void log(ThirdExchangeLog log);
}
