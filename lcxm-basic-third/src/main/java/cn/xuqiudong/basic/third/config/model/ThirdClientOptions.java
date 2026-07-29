package cn.xuqiudong.basic.third.config.model;

import java.net.Proxy;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 第三方出站运行参数。
 *
 * <p>公共模块只提供通用 HTTP 参数；baseUrl、密钥、业务参数等由项目侧自行维护。</p>
 *
 * @author Vic.xu
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
     * 追加一个默认 header。
     */
    public ThirdClientOptions addDefaultHeader(String name, String value) {
        if (name != null && !name.isBlank() && value != null) {
            this.defaultHeaders.put(name, value);
        }
        return this;
    }
}
