package cn.xuqiudong.basic.third.outbound.executor;

import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.log.service.OutboundExchangeLogger;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 出站执行器工厂。
 *
 * <p>工厂持有系统级通用配置，例如 ObjectMapper、默认 slf4j 日志开关、日志裁剪长度和统一自定义日志处理器。
 * 每个 partner 创建执行器时传入自己的 {@link ThirdClientOptions}。</p>
 *
 * @author Vic.xu
 */
public class OutboundExecutorFactory {

    private final ObjectMapper objectMapper;

    private final boolean printSlf4jLog;

    private final int slf4jTextMaxLength;

    private final OutboundExchangeLogger customLogger;

    public OutboundExecutorFactory(ObjectMapper objectMapper, boolean printSlf4jLog, int slf4jTextMaxLength,
            OutboundExchangeLogger customLogger) {
        this.objectMapper = objectMapper;
        this.printSlf4jLog = printSlf4jLog;
        this.slf4jTextMaxLength = slf4jTextMaxLength;
        this.customLogger = customLogger;
    }

    /**
     * 使用当前 partner 的 HTTP 配置创建 Hutool 出站执行器。
     */
    public OutboundExecutor create(ThirdClientOptions options) {
        return new HttpOutboundExecutor(options, objectMapper, printSlf4jLog, slf4jTextMaxLength, customLogger);
    }
}
