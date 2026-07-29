package cn.xuqiudong.basic.third.config.spring;

import cn.xuqiudong.basic.third.log.service.CompositeThirdExchangeLogger;
import cn.xuqiudong.basic.third.log.service.Slf4jThirdExchangeLogger;
import cn.xuqiudong.basic.third.log.service.ThirdExchangeLogger;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方出站 Spring 配置基类。
 *
 * <p>本类不加 {@code @Configuration}，具体项目的配置类继承它并自行添加
 * {@code @Configuration}。默认提供 slf4j 交换日志。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractThirdOutboundConfiguration {

    /**
     * 出站交换日志处理器。
     *
     * <p>默认组合 slf4j 日志。需要落库、MQ 或审计时，覆盖 {@link #customThirdExchangeLogger()}。</p>
     */
    @Bean
    public ThirdExchangeLogger thirdExchangeLogger() {
        List<ThirdExchangeLogger> loggers = new ArrayList<>();
        if (printThirdExchangeLog()) {
            loggers.add(new Slf4jThirdExchangeLogger(thirdSlf4jExchangeLogTextMaxLength()));
        }
        ThirdExchangeLogger customLogger = customThirdExchangeLogger();
        if (customLogger != null) {
            loggers.add(customLogger);
        }
        return new CompositeThirdExchangeLogger(loggers);
    }

    /**
     * 是否打印 slf4j 出站交换日志。
     */
    protected boolean printThirdExchangeLog() {
        return true;
    }

    /**
     * slf4j 打印请求体、响应体、异常信息时的最大长度；小于等于 0 表示不裁剪。
     */
    protected int thirdSlf4jExchangeLogTextMaxLength() {
        return Slf4jThirdExchangeLogger.DEFAULT_TEXT_MAX_LENGTH;
    }

    /**
     * 项目自定义出站交换日志处理器。
     *
     * <p>默认不处理。项目需要落库、MQ 或审计时，返回自己的 {@link ThirdExchangeLogger} 实现。</p>
     */
    protected ThirdExchangeLogger customThirdExchangeLogger() {
        return null;
    }
}
