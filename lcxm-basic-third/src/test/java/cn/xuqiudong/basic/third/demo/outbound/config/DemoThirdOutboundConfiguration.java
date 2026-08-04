package cn.xuqiudong.basic.third.demo.outbound.config;

import cn.xuqiudong.basic.third.config.spring.AbstractThirdOutboundConfiguration;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.client.DemoOutboundClient;
import cn.xuqiudong.basic.third.outbound.executor.HttpOutboundExecutor;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.log.service.OutboundExchangeLogger;
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
     * demo 厂商 Client 装配。
     */
    @Bean
    public DemoOutboundClient demoOutboundClient() {
        ThirdClientOptions options = new ThirdClientOptions();
        OutboundExecutor executor = new HttpOutboundExecutor(options, null, printOutboundExchangeLog(),
                outboundSlf4jExchangeLogTextMaxLength(), customOutboundExchangeLogger());
        return new DemoOutboundClient(options, executor);
    }

    /**
     * demo 不额外落库；真实项目可返回 DB/MQ/审计 logger。
     */
    @Override
    protected OutboundExchangeLogger customOutboundExchangeLogger() {
        return null;
    }
}
