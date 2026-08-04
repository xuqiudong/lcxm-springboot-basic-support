package cn.xuqiudong.basic.third.inbound.log.service;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.third.inbound.log.model.InboundInvokeLog;
import cn.xuqiudong.basic.third.inbound.log.model.InboundInvokeLogStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 第三方入站调用日志的 slf4j 实现。
 *
 * <p>仅用于开发排查；项目落库应实现 {@link InboundInvokeLogger}。</p>
 *
 * @author Vic.xu
 */
public class Slf4jInboundInvokeLogger implements InboundInvokeLogger {

    public static final int DEFAULT_TEXT_MAX_LENGTH = 4000;

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jInboundInvokeLogger.class);

    private final int textMaxLength;

    public Slf4jInboundInvokeLogger() {
        this(DEFAULT_TEXT_MAX_LENGTH);
    }

    public Slf4jInboundInvokeLogger(int textMaxLength) {
        this.textMaxLength = textMaxLength;
    }

    @Override
    public void log(InboundInvokeLog log) {
        if (log == null) {
            return;
        }
        if (InboundInvokeLogStatus.FAILED.equals(log.getStatus())) {
            LOGGER.warn("[third-inbound] requestId={}, third={}, operation={}, status={}, elapsed={}ms, context={}, request={}, response={}, exception={}, stack={}",
                    log.getRequestId(), log.getThirdCode(), log.getOperation(), log.getStatus(), log.getElapsedMillis(),
                    log.getContext(), abbreviate(log.getRequestParams()), abbreviate(log.getResponseParams()),
                    abbreviate(log.getExceptionMessage()), abbreviate(log.getExceptionStack()));
            return;
        }
        LOGGER.info("[third-inbound] requestId={}, third={}, operation={}, status={}, elapsed={}ms, context={}, request={}, response={}",
                log.getRequestId(), log.getThirdCode(), log.getOperation(), log.getStatus(), log.getElapsedMillis(),
                log.getContext(), abbreviate(log.getRequestParams()), abbreviate(log.getResponseParams()));
    }

    /**
     * 只裁剪 slf4j 打印内容，不改变日志模型本身。
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
