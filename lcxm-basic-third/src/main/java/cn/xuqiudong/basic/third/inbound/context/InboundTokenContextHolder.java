package cn.xuqiudong.basic.third.inbound.context;

import java.util.Date;

import cn.xuqiudong.basic.third.inbound.model.TokenValue;

/**
 * 当前入站请求的 token 身份上下文。
 *
 * <p>上下文只在当前请求线程内有效，由 token 拦截器绑定并在请求结束时清理。</p>
 *
 * @author Vic.xu
 */
public final class InboundTokenContextHolder {

    private static final ThreadLocal<TokenValue> HOLDER = new ThreadLocal<>();

    private InboundTokenContextHolder() {
    }

    /**
     * 绑定已通过校验的 token 身份。保存副本以避免调用方修改 TokenStore 中的对象。
     */
    public static void bind(TokenValue tokenValue) {
        if (tokenValue == null) {
            clear();
            return;
        }
        Date expireAt = tokenValue.getExpireAt();
        HOLDER.set(new TokenValue(tokenValue.getAppId(), tokenValue.getThirdCode(), tokenValue.getUsername(),
                expireAt == null ? null : new Date(expireAt.getTime())));
    }

    /**
     * 获取当前请求的 token 身份；未经过 token 拦截器或校验未通过时返回 {@code null}。
     */
    public static TokenValue current() {
        return HOLDER.get();
    }

    /**
     * 获取当前请求的 username；不存在 token 身份时返回 {@code null}。
     */
    public static String currentUsername() {
        TokenValue tokenValue = current();
        return tokenValue == null ? null : tokenValue.getUsername();
    }

    /**
     * 清理当前线程上下文，避免线程复用导致身份串扰。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
