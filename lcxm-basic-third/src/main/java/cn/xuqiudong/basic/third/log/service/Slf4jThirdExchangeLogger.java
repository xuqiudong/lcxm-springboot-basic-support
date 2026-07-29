package cn.xuqiudong.basic.third.log.service;

import cn.xuqiudong.basic.third.log.model.ThirdExchangeLog;
import cn.xuqiudong.basic.third.log.model.ThirdExchangeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * slf4j 出站交换日志实现。
 *
 * @author Vic.xu
 */
public class Slf4jThirdExchangeLogger implements ThirdExchangeLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jThirdExchangeLogger.class);

    /**
     * 成功日志使用 info，失败日志使用 warn。
     */
    @Override
    public void log(ThirdExchangeLog log) {
        if (log == null) {
            return;
        }
        if (ThirdExchangeStatus.FAILED.equals(log.getStatus())) {
            LOGGER.warn("[third-exchange] third={}, operation={}, method={}, url={}, httpStatus={}, status={}, elapsed={}ms, error={}",
                    log.getThirdCode(), log.getOperation(), log.getMethod(), log.getUrl(),
                    log.getHttpStatus(), log.getStatus(), log.getElapsedMillis(), log.getErrorMessage());
            return;
        }
        LOGGER.info("[third-exchange] third={}, operation={}, method={}, url={}, httpStatus={}, status={}, elapsed={}ms",
                log.getThirdCode(), log.getOperation(), log.getMethod(), log.getUrl(),
                log.getHttpStatus(), log.getStatus(), log.getElapsedMillis());
    }
}
