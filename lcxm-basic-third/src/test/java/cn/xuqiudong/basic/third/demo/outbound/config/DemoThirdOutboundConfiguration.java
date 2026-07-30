package cn.xuqiudong.basic.third.demo.outbound.config;

import java.time.Duration;

import cn.xuqiudong.basic.third.config.spring.AbstractThirdOutboundConfiguration;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.client.DemoOutboundClient;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.config.DemoOutboundConfig;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.config.DemoOutboundConfigProvider;
import cn.xuqiudong.basic.third.log.service.ThirdExchangeLogger;
import cn.xuqiudong.basic.third.outbound.config.CachingOutboundPartnerConfigProvider;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfigProvider;
import cn.xuqiudong.basic.third.outbound.executor.HttpOutboundExecutor;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 出站 demo 全局配置。
 *
 * <p>真实项目继承 {@link AbstractThirdOutboundConfiguration} 后，在这里统一配置交换日志；
 * 具体厂商 Client 放到 partner 子包中。</p>
 */
@Configuration
public class DemoThirdOutboundConfiguration extends AbstractThirdOutboundConfiguration {

    /**
     * demo 厂商配置来源。
     */
    @Bean
    public OutboundPartnerConfigProvider<DemoOutboundConfig> demoOutboundConfigProvider() {
        return new CachingOutboundPartnerConfigProvider<>(new DemoOutboundConfigProvider(), Duration.ofMinutes(5));
    }

    /**
     * demo 厂商 Client 装配。
     */
    @Bean
    public DemoOutboundClient demoOutboundClient(OutboundPartnerConfigProvider<DemoOutboundConfig> configProvider,
            ThirdExchangeLogger exchangeLogger) {
        ThirdClientOptions options = new ThirdClientOptions();
        OutboundExecutor executor = new HttpOutboundExecutor(options, null, exchangeLogger);
        return new DemoOutboundClient(configProvider, options, executor);
    }

    /**
     * demo 不额外落库；真实项目可返回 DB/MQ/审计 logger。
     */
    @Override
    protected ThirdExchangeLogger customThirdExchangeLogger() {
        return null;
    }
}
