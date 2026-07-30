package cn.xuqiudong.basic.third.demo.outbound.partner.demo.config;

import java.time.Duration;

import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfigProvider;

/**
 * 出站 demo 配置提供者。
 *
 * <p>真实项目可从配置文件、数据库、Redis 或配置中心读取。</p>
 */
public class DemoOutboundConfigProvider implements OutboundPartnerConfigProvider<DemoOutboundConfig> {

    @Override
    public DemoOutboundConfig getConfig(ThirdIdentity thirdIdentity) {
        DemoOutboundConfig config = new DemoOutboundConfig();
        config.setHost("https://third.example.com");
        config.setClientId("demo-client-id");
        config.setClientSecret("demo-client-secret");
        config.setClientOptions(new ThirdClientOptions()
                .setRequestTimeout(Duration.ofSeconds(20))
                .addDefaultHeader("X-Demo-Client", "demo"));
        return config;
    }
}
