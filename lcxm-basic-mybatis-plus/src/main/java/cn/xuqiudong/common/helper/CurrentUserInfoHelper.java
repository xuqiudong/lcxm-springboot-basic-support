package cn.xuqiudong.common.helper;

import java.util.function.Supplier;

/**
 * 描述:
 * 获取当前用户信息工具类
 * 建议在项目启动后 设置获取用户的方式， 将用于 通用字段填充
 *
 * @author Vic.xu
 * @since 2025-10-27
 */
public class CurrentUserInfoHelper {

    /**
     * 是否匿名访问
     */
    private static Supplier<Boolean> isAnonymousSupplier = () -> true;

    /**
     * 用户ID
     */
    private static Supplier<String> userIdSupplier = () -> "-";
    /**
     * 用户名
     */
    private static Supplier<String> usernameSupplier = () -> "anonymous";

    public static boolean isAnonymous() {
        return isAnonymousSupplier.get();
    }

    public static String getUserId() {
        return userIdSupplier.get();
    }

    public static String getUsername() {
        return usernameSupplier.get();
    }


    public static void setIsAnonymousSupplier(Supplier<Boolean> isAnonymousSupplier) {
        CurrentUserInfoHelper.isAnonymousSupplier = isAnonymousSupplier;
    }

    public static void setUserIdSupplier(Supplier<String> userIdSupplier) {
        CurrentUserInfoHelper.userIdSupplier = userIdSupplier;
        if (userIdSupplier != null) {
            CurrentUserInfoHelper.isAnonymousSupplier = () -> false;
        }
    }

    public static void setUsernameSupplier(Supplier<String> usernameSupplier) {
        CurrentUserInfoHelper.usernameSupplier = usernameSupplier;
        if (usernameSupplier != null) {
            CurrentUserInfoHelper.isAnonymousSupplier = () -> false;
        }
    }
}
