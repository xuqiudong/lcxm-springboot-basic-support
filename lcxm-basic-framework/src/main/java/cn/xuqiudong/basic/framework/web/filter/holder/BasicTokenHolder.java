package cn.xuqiudong.basic.framework.web.filter.holder;

import cn.xuqiudong.basic.framework.web.filter.model.BasicToken;

/**
 * 描述:
 *   BasicToken 线程持有者
 * @author Vic.xu
 * @since 2024-09-03 14:23
 */
public class BasicTokenHolder {

    private static final ThreadLocal<BasicToken> BASIC_TOKEN_THREAD_LOCAL = new ThreadLocal<>();


    private BasicTokenHolder() {
    }

    /**
     * 获取当前线程的token
     */
    public static BasicToken getToken() {
        return BASIC_TOKEN_THREAD_LOCAL.get();
    }

    /**
     * 设置当前线程的token
     */
    public static void setToken(BasicToken basicToken) {
        BASIC_TOKEN_THREAD_LOCAL.set(basicToken);
    }

    /**
     * 清除当前线程的token
     */
    public static void clear() {
        BASIC_TOKEN_THREAD_LOCAL.remove();
    }
}
