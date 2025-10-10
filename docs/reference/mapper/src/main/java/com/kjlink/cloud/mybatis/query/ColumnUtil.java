package com.kjlink.cloud.mybatis.query;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.support.IdeaProxyLambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.ShadowLambdaMeta;

/**
 * pojo反射lambda来获取字段名称
 *
 * @author kj
 * @since 2023-02-07
 */
public class ColumnUtil {
    private static final Map<String, String> FIELD_NAME_CACHE = new ConcurrentHashMap<>();
    //安全的列名称，防止SQL注入
    private static final Pattern SAFE_COLUMN_NAME = Pattern.compile("[A-Za-z0-9\\._]+");
    private static final char EMPTY = Character.SPACE_SEPARATOR;
    //缓存列名称
    private static Map<String, String> underlineColumns = new ConcurrentHashMap<>();

    private ColumnUtil() {
        //sonar
    }

    private static void checkSafe(String column) {
        Assert.isTrue(SAFE_COLUMN_NAME.matcher(column).matches(), "不安全的列名称：\"{}\"", column);
    }

    public static <T> String safeColumn(char tableAlias, Column<T, ?> column) {
        if (tableAlias == EMPTY) {
            return safeColumn(column);
        }
        //表别名为a-z的单个字母
        Assert.isTrue(tableAlias >= 'a' && tableAlias <= 'z', "不支持的表别名：\"{}\"", tableAlias);
        return tableAlias + "." + safeColumn(column);
    }

    public static <T> String safeColumn(Column<T, ?> column) {
        String name = ColumnUtil.getFieldName(column);
        return safeColumn(name);
    }

    public static String safeColumn(String column) {
        Assert.notBlank(column, "列名为空");

        return underlineColumns.computeIfAbsent(column, (col) -> {
            checkSafe(column);
            return StrUtil.toUnderlineCase(column);
        });
    }

    /**
     * 获取lambda setter对应的字段名称
     *
     * @param setter
     * @param <T>
     * @param <U>
     * @return
     * @throws IllegalArgumentException
     */
    static <T, U> String getFieldName(Column<T, U> setter) throws IllegalArgumentException {
        String className = setter.getClass().getName();
        return FIELD_NAME_CACHE.computeIfAbsent(className, (cn) -> resolveFieldName(setter));
    }

    private static String resolveFieldName(Serializable serializable) {
        String methodName = getImplMethodName(serializable);
        return BeanUtil.getFieldName(methodName);
    }

    /**
     * 获取lambda方法名称
     *
     * @param func 需要解析的 lambda 对象
     * @return 返回解析后的结果
     */
    private static String getImplMethodName(Serializable func) {
        // 1. IDEA 调试模式下 lambda 表达式是一个代理
        if (func instanceof Proxy) {
            return new IdeaProxyLambdaMeta((Proxy) func).getImplMethodName();
        }
        // 2. 反射读取
        try {
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            SerializedLambda serializedLambda = (SerializedLambda) ReflectionKit.setAccessible(method).invoke(func);
            return serializedLambda.getImplMethodName();
        } catch (Throwable e) {
            // 3. 反射失败使用序列化的方式读取
            com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda serializedLambda =
                    com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda.extract(func);
            return new ShadowLambdaMeta(serializedLambda).getImplMethodName();
        }
    }
}
