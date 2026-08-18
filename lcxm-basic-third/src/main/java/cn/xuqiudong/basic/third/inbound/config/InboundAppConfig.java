package cn.xuqiudong.basic.third.inbound.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 第三方入站 app 配置。
 *
 * <p>业务项目通过 {@code InboundAppConfigRegistry} 提供该配置。</p>
 *
 * @author Vic.xu
 */
@Data
public class InboundAppConfig {

    /**
     * 第三方请求我方接口时使用的 appId。
     */
    private String appId;

    /**
     * 项目内部第三方标识，用于日志、排查和业务区分。
     */
    private String thirdCode;

    /**
     * 第三方公钥，用于校验第三方私钥生成的 sign。
     */
    private String publicKey;

    /**
     * 允许访问的 username；为空表示不限制 username。
     */
    private Set<String> usernames = new LinkedHashSet<>();

    /**
     * 自定义 username 校验回调；配置后优先于 usernames 白名单。
     */
    private InboundUsernameChecker usernameChecker;

    /**
     * token 有效期，默认 1 小时。
     */
    private Duration tokenTtl = Duration.ofHours(1);

    /**
     * timestamp 允许偏差，默认 5 分钟。
     */
    private Duration timestampTolerance = Duration.ofMinutes(5);

    /**
     * 是否启用 nonce 防重，默认关闭。
     */
    private boolean nonceRequired = false;

    /**
     * 追加一个允许访问的 username。
     */
    public InboundAppConfig addUsername(String username) {
        if (StrUtil.isNotBlank(username)) {
            this.usernames.add(username);
        }
        return this;
    }

    /**
     * 追加一个usernames (逗号分隔的)
     */
    public InboundAppConfig addUsernames(String usernames) {
        if (StrUtil.isNotBlank(usernames)) {
            String[] split = usernames.split(",");
            for (String username : split) {
                this.addUsername(username);
            }
        }
        return this;
    }

    /**
     * 判断当前 username 是否允许访问。
     */
    public boolean allowsUsername(String username) {
        if (usernameChecker != null) {
            return usernameChecker.checkUsername(username);
        }
        if (usernames == null || usernames.isEmpty()) {
            return true;
        }
        return usernames.stream().anyMatch(item -> item.equalsIgnoreCase(username));
    }
}
