package cn.xuqiudong.basic.third.config.spring;

import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutorFactory;
import cn.xuqiudong.basic.third.outbound.log.service.OutboundExchangeLogger;
import cn.xuqiudong.basic.third.outbound.log.service.Slf4jOutboundExchangeLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

/**
 * 第三方出站 Spring 配置基类。
 *
 * <p>本类不加 {@code @Configuration}，具体项目的配置类继承后自行添加。
 * 本类只负责创建系统级 {@link OutboundExecutorFactory}，不装配具体 partner。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractThirdOutboundConfiguration {

    /**
     * 创建出站执行器工厂。
     *
     * <p>每个 partner 仍然通过自己的 ThirdClientOptions 决定超时、代理、默认 header、是否记录交换日志等。</p>
     */
    @Bean
    public OutboundExecutorFactory outboundExecutorFactory() {
        return new OutboundExecutorFactory(outboundObjectMapper(), printOutboundExchangeLog(),
                outboundSlf4jExchangeLogTextMaxLength(), customOutboundExchangeLogger());
    }

    /**
     * 出站 JSON ObjectMapper；返回 null 时使用 HttpOutboundExecutor 默认配置。
     */
    protected ObjectMapper outboundObjectMapper() {
        return null;
    }

    /**
     * 是否打印默认 slf4j 出站交换日志。
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
     */
    protected OutboundExchangeLogger customOutboundExchangeLogger() {
        return null;
    }
}
