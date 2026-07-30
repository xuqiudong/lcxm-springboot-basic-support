package cn.xuqiudong.basic.third.demo.inbound.partner.demo.config;

import cn.xuqiudong.basic.third.inbound.config.InboundAppConfig;
import cn.xuqiudong.basic.third.inbound.registry.InboundAppConfigRegistry;
import org.springframework.stereotype.Component;

/**
 * 入站 demo 第三方配置注册点。
 *
 * <p>真实项目通常一个第三方一个 registry，配置来源可以是配置文件、数据库或配置中心。</p>
 */
@Component
public class DemoInboundAppConfigRegistry implements InboundAppConfigRegistry {

    @Override
    public String thirdCode() {
        return "demo-partner";
    }

    @Override
    public InboundAppConfig inboundConfig() {
        InboundAppConfig config = new InboundAppConfig();
        config.setAppId("demo-app-id");
        config.setThirdCode(thirdCode());
        config.setPublicKey("replace-with-third-public-key");
        config.addUsername("demo-user");
        config.setNonceRequired(true);
        return config;
    }
}
