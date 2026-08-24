package cn.xuqiudong.basic.secureid.util;

import java.time.temporal.TemporalAccessor;

/**
 * Secure ID 递归处理中的类型判断工具。
 */
public final class SecureIdClassUtils {

    private SecureIdClassUtils() {
    }

    /**
     * 判断类型是否为不需要继续反射展开的简单值。
     * <p>
     * String 类型字段如果标注了 @SecureId，会在字段处理阶段加密；
     * 如果只是普通 String 字段，则不会继续作为对象展开。
     */
    public static boolean isSimpleValue(Class<?> type) {
        return type.isPrimitive()
                || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class == type
                || Character.class == type
                || Enum.class.isAssignableFrom(type)
                || TemporalAccessor.class.isAssignableFrom(type)
                || Class.class == type;
    }
}
