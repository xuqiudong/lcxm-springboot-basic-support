package cn.xuqiudong.basic.third.outbound.config;

import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;

/**
 * 第三方出站配置基类。
 *
 * <p>基础模块只约束 host 和通用 HTTP 参数；账号、密钥、路径等厂商参数由业务项目子类自行扩展。</p>
 *
 * @author Vic.xu
 */
public interface OutboundPartnerConfig {

    /**
     * 第三方服务根地址，例如 {@code https://api.example.com}。
     */
    String getHost();

    /**
     * 当前第三方 HTTP 通用配置。
     */
    default ThirdClientOptions getClientOptions() {
        return new ThirdClientOptions();
    }
}
