package cn.xuqiudong.basic.third.config.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 第三方出站运行参数。
 *
 * <p>公共模块只维护通用 HTTP 参数。baseUrl、密钥、业务参数等由具体项目自行维护。</p>
 *
 * @author Vic.xu
 * @since 2026-07-29
 */
@Data
@Accessors(chain = true)
public class ThirdClientOptions {

    /**
     * 单次请求超时时间。
     */
    private Duration requestTimeout = Duration.ofSeconds(30);

    /**
     * 当前第三方所有请求共用的默认 header。
     */
    private Map<String, String> defaultHeaders = new LinkedHashMap<>();

    /**
     * 是否记录出站交换日志。
     */
    private boolean exchangeLogEnabled = true;

    /**
     * Hutool HTTP 代理配置。
     */
    private Proxy proxy;

    /**
     * 根据代理类型、主机和端口快捷设置 HTTP 代理。
     *
     * <p>类型不区分大小写，支持 {@code HTTP}、{@code SOCKS} 和 {@code DIRECT}。
     * 类型为空、类型为 {@code DIRECT}，或主机、端口无效时清除已有代理。</p>
     *
     * @param proxyType 代理类型
     * @param proxyHost 代理主机
     * @param proxyPort 代理端口
     * @return 当前配置对象
     * @throws IllegalArgumentException 代理类型无法识别时抛出
     */
    public ThirdClientOptions setProxy(String proxyType, String proxyHost, int proxyPort) {
        if (StrUtil.isBlank(proxyType)) {
            return setProxy((Proxy.Type) null, proxyHost, proxyPort);
        }

        Proxy.Type type = Proxy.Type.valueOf(proxyType.trim().toUpperCase(Locale.ROOT));
        return setProxy(type, proxyHost, proxyPort);
    }

    /**
     * 根据代理类型、主机和端口快捷设置 HTTP 代理。
     *
     * <p>类型为空、类型为 {@link Proxy.Type#DIRECT}，或主机、端口无效时清除已有代理。</p>
     *
     * @param proxyType 代理类型
     * @param proxyHost 代理主机
     * @param proxyPort 代理端口
     * @return 当前配置对象
     */
    public ThirdClientOptions setProxy(Proxy.Type proxyType, String proxyHost, int proxyPort) {
        this.proxy = null;
        if (proxyType != null && proxyType != Proxy.Type.DIRECT
                && StrUtil.isNotBlank(proxyHost) && proxyPort > 0) {
            this.proxy = new Proxy(proxyType, InetSocketAddress.createUnresolved(proxyHost.trim(), proxyPort));
        }
        return this;
    }

    /**
     * 追加一个默认 header。
     *
     * @param name  header 名称
     * @param value header 值
     * @return 当前配置对象
     */
    public ThirdClientOptions addDefaultHeader(String name, String value) {
        if (StrUtil.isNotBlank(name) && value != null) {
            this.defaultHeaders.put(name, value);
        }
        return this;
    }
}
