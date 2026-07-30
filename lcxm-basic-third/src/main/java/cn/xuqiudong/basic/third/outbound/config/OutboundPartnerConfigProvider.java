package cn.xuqiudong.basic.third.outbound.config;

import cn.xuqiudong.basic.third.common.model.ThirdIdentity;

/**
 * 第三方出站配置提供者。
 *
 * <p>具体项目可从配置文件、数据库、Redis 或配置中心读取配置。</p>
 *
 * @author Vic.xu
 */
public interface OutboundPartnerConfigProvider<C extends OutboundPartnerConfig> {

    /**
     * 获取指定第三方的出站配置。
     */
    C getConfig(ThirdIdentity thirdIdentity);
}
