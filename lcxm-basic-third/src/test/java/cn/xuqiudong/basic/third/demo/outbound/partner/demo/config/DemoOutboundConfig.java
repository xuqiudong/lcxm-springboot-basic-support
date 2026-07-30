package cn.xuqiudong.basic.third.demo.outbound.partner.demo.config;

import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfig;
import lombok.Data;

/**
 * 出站 demo 厂商配置。
 *
 * <p>真实项目可继续增加 clientId、secret、path 配置项、签名密钥等字段。</p>
 */
@Data
public class DemoOutboundConfig implements OutboundPartnerConfig {

    private String host;

    private String clientId;

    private String clientSecret;

    private ThirdClientOptions clientOptions = new ThirdClientOptions();
}
