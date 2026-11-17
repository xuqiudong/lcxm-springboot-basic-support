package cn.xuqiudong.basic.third.outbound.client;

import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfig;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;

/**
 * 第三方 Partner 的运行时快照。
 *
 * <p>厂商配置、HTTP 配置和执行器在同一次加载流程中创建，并作为一个整体缓存和失效，
 * 避免配置刷新后出现新 host 与旧代理、旧日志策略混用的问题。快照创建后不替换三个对象引用，
 * 其中的配置对象应按只读方式使用。</p>
 *
 * @param <C> 厂商配置类型
 * @author Vic.xu
 * @since 2026-11-17
 */
public class OutboundPartnerRuntime<C extends OutboundPartnerConfig> {

    /**
     * 当前快照对应的厂商配置。
     */
    private final C config;

    /**
     * 从当前厂商配置解析出的 HTTP 配置。
     */
    private final ThirdClientOptions options;

    /**
     * 使用当前 HTTP 配置创建的出站执行器。
     */
    private final OutboundExecutor executor;

    /**
     * 创建完整的 Partner 运行时快照。
     *
     * @param config   厂商配置
     * @param options  HTTP 配置
     * @param executor 出站执行器
     */
    OutboundPartnerRuntime(C config, ThirdClientOptions options, OutboundExecutor executor) {
        this.config = config;
        this.options = options;
        this.executor = executor;
    }

    /**
     * 获取厂商配置。
     *
     * @return 厂商配置
     */
    C getConfig() {
        return config;
    }

    /**
     * 获取 HTTP 配置。
     *
     * @return HTTP 配置
     */
    ThirdClientOptions getOptions() {
        return options;
    }

    /**
     * 获取出站执行器。
     *
     * @return 出站执行器
     */
    OutboundExecutor getExecutor() {
        return executor;
    }
}
