package cn.xuqiudong.basic.third.log.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.xuqiudong.basic.third.log.model.ThirdExchangeLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 组合出站交换日志处理器。
 *
 * <p>用于统一编排 slf4j、数据库、MQ、审计等多个日志处理器。</p>
 *
 * @author Vic.xu
 */
public class CompositeThirdExchangeLogger implements ThirdExchangeLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeThirdExchangeLogger.class);

    private final List<ThirdExchangeLogger> loggers;

    /**
     * 创建组合日志处理器。
     */
    public CompositeThirdExchangeLogger(Collection<ThirdExchangeLogger> loggers) {
        this.loggers = new ArrayList<>();
        if (loggers != null) {
            loggers.stream()
                    .filter(logger -> logger != null && logger != this)
                    .forEach(this.loggers::add);
        }
    }

    /**
     * 依次调用所有日志处理器。
     *
     * <p>单个日志处理器异常不会影响出站业务调用，也不会阻断后续日志处理器。</p>
     */
    @Override
    public void log(ThirdExchangeLog log) {
        for (ThirdExchangeLogger logger : loggers) {
            try {
                logger.log(log);
            } catch (Exception e) {
                LOGGER.warn("third exchange logger failed: {}", logger.getClass().getName(), e);
            }
        }
    }
}
