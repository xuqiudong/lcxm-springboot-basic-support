package cn.xuqiudong.basic.third.config.spring;

import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.config.provider.ThirdOptionsProvider;
import cn.xuqiudong.basic.third.log.model.ThirdExchangeLog;
import cn.xuqiudong.basic.third.log.service.Slf4jThirdExchangeLogger;
import cn.xuqiudong.basic.third.log.service.ThirdExchangeLogger;
import cn.xuqiudong.basic.third.outbound.executor.HttpOutboundExecutor;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import org.springframework.context.annotation.Bean;

/**
 * 第三方出站 Spring 配置基类。
 *
 * <p>本类不加 {@code @Configuration}，具体项目的配置类继承它并自行添加
 * {@code @Configuration}。默认提供出站配置 provider 和 slf4j 交换日志。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractThirdOutboundConfiguration {

    private final ThirdExchangeLogger defaultExchangeLogger = new Slf4jThirdExchangeLogger();

    /**
     * 出站运行参数 provider。
     */
    @Bean
    public ThirdOptionsProvider thirdOptionsProvider() {
        return this::thirdClientOptions;
    }

    /**
     * 出站交换日志处理器。
     *
     * <p>子类不要覆盖该 {@code @Bean} 方法。需要落库、MQ 或审计时，覆盖
     * {@link #saveThirdExchangeLog(ThirdExchangeLog)}。</p>
     */
    @Bean
    public ThirdExchangeLogger thirdExchangeLogger() {
        return this::recordThirdExchangeLog;
    }

    /**
     * 获取指定第三方的出站运行参数。
     *
     * <p>默认所有第三方共用 {@link #defaultThirdClientOptions()}。需要按厂商区分超时、
     * 默认 header 或日志开关时，覆盖该方法。</p>
     */
    protected ThirdClientOptions thirdClientOptions(String thirdCode) {
        return defaultThirdClientOptions();
    }

    /**
     * 默认出站运行参数。
     */
    protected ThirdClientOptions defaultThirdClientOptions() {
        return new ThirdClientOptions();
    }

    /**
     * 记录出站交换日志。
     */
    protected void recordThirdExchangeLog(ThirdExchangeLog log) {
        if (printThirdExchangeLog()) {
            defaultExchangeLogger.log(log);
        }
        saveThirdExchangeLog(log);
    }

    /**
     * 是否打印 slf4j 出站交换日志。
     */
    protected boolean printThirdExchangeLog() {
        return true;
    }

    /**
     * 保存出站交换日志。
     *
     * <p>默认不落库。项目需要落库、MQ 或审计时，覆盖该方法。</p>
     */
    protected void saveThirdExchangeLog(ThirdExchangeLog log) {
        // default no persistence
    }

    /**
     * 为某个第三方创建默认 Hutool HTTP 执行器。
     *
     * <p>业务项目声明具体第三方 Client bean 时可直接调用该方法。</p>
     */
    protected OutboundExecutor thirdOutboundExecutor(String thirdCode, ThirdOptionsProvider optionsProvider,
            ThirdExchangeLogger exchangeLogger) {
        return new HttpOutboundExecutor(optionsProvider.getOptions(thirdCode), null, exchangeLogger);
    }
}
