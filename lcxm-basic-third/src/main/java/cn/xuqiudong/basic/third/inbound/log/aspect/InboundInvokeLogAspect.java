package cn.xuqiudong.basic.third.inbound.log.aspect;

import java.util.Map;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.third.inbound.log.annotation.InboundLog;
import cn.xuqiudong.basic.third.inbound.log.context.InboundInvokeContextHolder;
import cn.xuqiudong.basic.third.inbound.log.model.InboundInvokeLog;
import cn.xuqiudong.basic.third.inbound.log.model.InboundInvokeLogStatus;
import cn.xuqiudong.basic.third.inbound.log.service.InboundInvokeLogger;
import cn.xuqiudong.basic.third.inbound.log.service.Slf4jInboundInvokeLogger;
import cn.xuqiudong.basic.third.inbound.log.util.InboundInvokeLogUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 第三方入站业务方法出入参和异常日志切面，是入站调用日志的核心入口。
 *
 * <p>只处理带 {@link InboundLog} 的方法；slf4j 打印和自定义入库在这里直接执行，不再额外组合一层 logger。</p>
 *
 * @author Vic.xu
 */
@Aspect
public class InboundInvokeLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundInvokeLogAspect.class);

    private final Slf4jInboundInvokeLogger slf4jLogger;

    private final InboundInvokeLogger customLogger;

    private final Class<?>[] extraIgnoreTypes;

    /**
     * 创建入站日志切面。
     *
     * @param printSlf4jLog 是否打印默认 slf4j 日志
     * @param slf4jTextMaxLength slf4j 打印文本最大长度，小于等于 0 表示不裁剪
     * @param customLogger 项目自定义处理器，可为 null；通常用于入库、MQ、审计
     */
    public InboundInvokeLogAspect(boolean printSlf4jLog, int slf4jTextMaxLength, InboundInvokeLogger customLogger) {
        this(printSlf4jLog, slf4jTextMaxLength, customLogger, new Class<?>[0]);
    }

    /**
     * 创建入站日志切面，并补充当前项目不适合序列化记录的参数类型。
     */
    public InboundInvokeLogAspect(boolean printSlf4jLog, int slf4jTextMaxLength, InboundInvokeLogger customLogger,
            Class<?>... extraIgnoreTypes) {
        this.slf4jLogger = printSlf4jLog ? new Slf4jInboundInvokeLogger(slf4jTextMaxLength) : null;
        this.customLogger = customLogger;
        this.extraIgnoreTypes = extraIgnoreTypes == null ? new Class<?>[0] : extraIgnoreTypes;
    }

    /**
     * 记录方法入参、出参、异常和耗时。
     */
    @Around("@annotation(inboundLog)")
    public Object around(ProceedingJoinPoint joinPoint, InboundLog inboundLog) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        InboundInvokeLog log = new InboundInvokeLog()
                .setRequestId(IdUtil.fastSimpleUUID())
                .setThirdCode(StrUtil.emptyToNull(inboundLog.thirdCode()))
                .setOperation(inboundLog.operation());

        if (inboundLog.request()) {
            log.setRequestParams(InboundInvokeLogUtils.serializeArgs(signature.getMethod(), joinPoint.getArgs(),
                    extraIgnoreTypes));
        }

        try {
            Object result = joinPoint.proceed();
            log.setStatus(InboundInvokeLogStatus.SUCCESS);
            if (inboundLog.response()) {
                if (Void.TYPE.equals(signature.getReturnType()) || Void.class.equals(signature.getReturnType())) {
                    log.setResponseParams("[void]");
                } else {
                    log.setResponseParams(InboundInvokeLogUtils.serializeValue(result, extraIgnoreTypes));
                }
            }
            return result;
        } catch (Throwable error) {
            log.setStatus(InboundInvokeLogStatus.FAILED);
            log.setExceptionClass(error.getClass().getName());
            log.setExceptionMessage(error.getMessage());
            if (inboundLog.exceptionStack()) {
                log.setExceptionStack(InboundInvokeLogUtils.stackTraceToString(error));
            }
            throw error;
        } finally {
            log.setElapsedMillis(System.currentTimeMillis() - start);
            Map<String, Object> context = InboundInvokeContextHolder.snapshot();
            log.setContext(context);
            Object thirdCode = context.get("thirdCode");
            if (thirdCode != null && StrUtil.isNotBlank(String.valueOf(thirdCode))) {
                log.setThirdCode(String.valueOf(thirdCode));
            }
            safeLog(log);
            InboundInvokeContextHolder.clear();
        }
    }

    /**
     * 日志记录失败不能影响原业务调用。
     */
    private void safeLog(InboundInvokeLog log) {
        safeLog("slf4j", slf4jLogger, log);
        safeLog("custom", customLogger, log);
    }

    /**
     * 某个日志落点失败时只打印告警，不影响第三方接口本身。
     */
    private void safeLog(String loggerName, InboundInvokeLogger logger, InboundInvokeLog log) {
        if (logger == null) {
            return;
        }
        try {
            logger.log(log);
        } catch (RuntimeException e) {
            LOGGER.warn("third inbound invoke {} log failed", loggerName, e);
        }
    }
}
