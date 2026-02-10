package cn.xuqiudong.common.base.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 描述:
 * 获取当前用户信息工具类
 * 建议在项目启动后 设置获取用户的方式， 将用于 通用字段填充
 *
 * @author Vic.xu
 * @since 2025-10-27
 */
public class CurrentUserInfoContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserInfoContext.class);

    /**
     * 是否匿名访问
     */
    private static boolean isAnonymous = true;

    /**
     * 用户ID
     */
    private static Supplier<String> userIdSupplier = () -> "-1";
    /**
     * 用户名
     */
    private static Supplier<String> usernameSupplier = () -> "anonymous";

    /**
     * 是否匿名访问
     */
    public static boolean isAnonymous() {
        return isAnonymous;
    }

    /**
     * 获取当前用户ID
     */
    public static String getUserId() {
        try {
            return userIdSupplier.get();
        } catch (Exception e) {
            LOGGER.warn("获取当前用户ID异常:{}", e.getMessage());
            return "-1";
        }
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        try {
            return usernameSupplier.get();
        } catch (Exception e) {
            LOGGER.warn("获取当前用户名异常:{}", e.getMessage());
            return "anonymous";
        }
    }


    public static void setIsAnonymous(boolean isAnonymous) {
        CurrentUserInfoContext.isAnonymous = isAnonymous;
    }

    public static void setUserIdSupplier(Supplier<String> userIdSupplier) {
        CurrentUserInfoContext.userIdSupplier = userIdSupplier;
        if (userIdSupplier != null) {
            CurrentUserInfoContext.isAnonymous = false;
        }
    }

    public static void setUsernameSupplier(Supplier<String> usernameSupplier) {
        CurrentUserInfoContext.usernameSupplier = usernameSupplier;
        if (usernameSupplier != null) {
            CurrentUserInfoContext.isAnonymous = false;
        }
    }
}
