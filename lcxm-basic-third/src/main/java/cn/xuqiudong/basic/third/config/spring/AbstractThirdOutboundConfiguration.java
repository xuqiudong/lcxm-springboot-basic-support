package cn.xuqiudong.basic.third.config.spring;

import cn.xuqiudong.basic.third.outbound.log.service.OutboundExchangeLogger;
import cn.xuqiudong.basic.third.outbound.log.service.Slf4jOutboundExchangeLogger;

/**
 * 第三方出站 Spring 配置基类。
 *
 * <p>本类不加 {@code @Configuration}，具体项目的配置类继承它并自行添加。
 * 默认提供出站交换日志的 slf4j 开关和自定义处理扩展点。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractThirdOutboundConfiguration {

    /**
     * 是否打印 slf4j 出站交换日志。
     */
    protected boolean printOutboundExchangeLog() {
        return true;
    }

    /**
     * slf4j 打印请求体、响应体、异常信息时的最大长度；小于等于 0 表示不裁剪。
     */
    protected int outboundSlf4jExchangeLogTextMaxLength() {
        return Slf4jOutboundExchangeLogger.DEFAULT_TEXT_MAX_LENGTH;
    }

    /**
     * 项目自定义出站交换日志处理器。
     *
     * <p>默认不处理。项目需要落库、MQ 或审计时，返回自己的 {@link OutboundExchangeLogger} 实现。
     * 出站执行器会直接调用默认 slf4j 和该自定义处理器，不再额外组合一层 logger。</p>
     */
    protected OutboundExchangeLogger customOutboundExchangeLogger() {
        return null;
    }
}
