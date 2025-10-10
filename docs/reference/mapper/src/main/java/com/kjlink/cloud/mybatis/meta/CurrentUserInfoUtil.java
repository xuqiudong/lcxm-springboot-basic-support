package com.kjlink.cloud.mybatis.meta;

import java.util.function.Supplier;

/**
 * 获取当前用户信息工具类
 *
 * @author Fulai
 * @since 2025-05-06
 */
public class CurrentUserInfoUtil {
    private static Supplier<Boolean> isAnonymousSupplier = () -> true;
    private static Supplier<String> usernameSupplier = () -> "anonymous";
    private static Supplier<String> organizationIdSupplier = () -> "-1";

    public static boolean isAnonymous() {
        return isAnonymousSupplier.get();
    }

    public static String getUsername() {
        return usernameSupplier.get();
    }

    public static String getOrganizationId() {
        return organizationIdSupplier.get();
    }

    public static void setIsAnonymousSupplier(Supplier<Boolean> isAnonymousSupplier) {
        CurrentUserInfoUtil.isAnonymousSupplier = isAnonymousSupplier;
    }

    public static void setOrganizationIdSupplier(Supplier<String> organizationIdSupplier) {
        CurrentUserInfoUtil.organizationIdSupplier = organizationIdSupplier;
    }

    public static void setUsernameSupplier(Supplier<String> usernameSupplier) {
        CurrentUserInfoUtil.usernameSupplier = usernameSupplier;
    }
}
