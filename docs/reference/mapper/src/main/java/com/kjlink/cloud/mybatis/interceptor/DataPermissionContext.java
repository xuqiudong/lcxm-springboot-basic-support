package com.kjlink.cloud.mybatis.interceptor;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;

/**
 * 使用ThreadLocal方式启用数据权限拦截器
 *
 * @author Fulai
 * @since 2025-07-10
 */
public class DataPermissionContext {
    private static final ThreadLocal<Class<? extends DataPermissionHandler>> LOCAL_HANDLER = new ThreadLocal<>();

    /**
     * 数据权限设置到线程上，只能管一次mapper方法调用
     * @param handlerClass
     */
    public static void enable(Class<? extends DataPermissionHandler> handlerClass) {
        LOCAL_HANDLER.set(handlerClass);
    }

    /**
     * 获取并删除ThreadLocal
     * @return
     */
    public static Class<? extends DataPermissionHandler> getAndRemove() {
        Class<? extends DataPermissionHandler> config = LOCAL_HANDLER.get();
        if (config != null) {
            LOCAL_HANDLER.remove();
        }
        return config;
    }
}
