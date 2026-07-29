package cn.xuqiudong.basic.third.inbound.registry;

import cn.xuqiudong.basic.third.inbound.config.InboundAppConfig;

/**
 * 第三方入站配置注册点。
 *
 * <p>业务项目通常按第三方拆分实现类：一个第三方一个 Registry。</p>
 *
 * @author Vic.xu
 */
public interface InboundAppConfigRegistry {

    /**
     * 第三方业务标识，用于日志、排查和项目侧区分厂商。
     */
    String thirdCode();

    /**
     * 当前第三方请求我方接口时使用的 app 配置。
     */
    InboundAppConfig inboundConfig();
}
