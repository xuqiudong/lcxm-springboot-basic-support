package cn.xuqiudong.basic.third.outbound.log.service;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.third.outbound.log.model.OutboundExchangeLog;
import cn.xuqiudong.basic.third.outbound.log.model.OutboundExchangeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * slf4j 出站交换日志实现。
 *
 * @author Vic.xu
 */
public class Slf4jOutboundExchangeLogger implements OutboundExchangeLogger {

    /**
     * 默认 slf4j 文本字段最大打印长度。
     */
    public static final int DEFAULT_TEXT_MAX_LENGTH = 4000;

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jOutboundExchangeLogger.class);

    private final int textMaxLength;

    /**
     * 使用默认裁剪长度创建 slf4j 日志处理器。
     */
    public Slf4jOutboundExchangeLogger() {
        this(DEFAULT_TEXT_MAX_LENGTH);
    }

    /**
     * 使用指定裁剪长度创建 slf4j 日志处理器；小于等于 0 表示不裁剪。
     */
    public Slf4jOutboundExchangeLogger(int textMaxLength) {
        this.textMaxLength = textMaxLength;
    }

    /**
     * 成功日志使用 info，失败日志使用 warn。
     */
    @Override
    public void log(OutboundExchangeLog log) {
        if (log == null) {
            return;
        }
        if (OutboundExchangeStatus.FAILED.equals(log.getStatus())) {
            LOGGER.warn("[third-outbound] third={}, operation={}, method={}, url={}, httpStatus={}, status={}, elapsed={}ms, requestBody={}, responseBody={}, error={}",
                    log.getThirdCode(), log.getOperation(), log.getMethod(), log.getUrl(),
                    log.getHttpStatus(), log.getStatus(), log.getElapsedMillis(),
                    abbreviate(log.getRequestBody()), abbreviate(log.getResponseBody()),
                    abbreviate(log.getErrorMessage()));
            return;
        }
        LOGGER.info("[third-outbound] third={}, operation={}, method={}, url={}, httpStatus={}, status={}, elapsed={}ms, requestBody={}, responseBody={}",
                log.getThirdCode(), log.getOperation(), log.getMethod(), log.getUrl(),
                log.getHttpStatus(), log.getStatus(), log.getElapsedMillis(),
                abbreviate(log.getRequestBody()), abbreviate(log.getResponseBody()));
    }

    /**
     * 只裁剪 slf4j 打印内容，不改变 OutboundExchangeLog 模型本身。
     */
    private String abbreviate(String text) {
        if (text == null || textMaxLength <= 0 || text.length() <= textMaxLength) {
            return text;
        }
        if (textMaxLength <= 3) {
            return StrUtil.subPre(text, textMaxLength);
        }
        return StrUtil.subPre(text, textMaxLength - 3) + "...";
    }
}
