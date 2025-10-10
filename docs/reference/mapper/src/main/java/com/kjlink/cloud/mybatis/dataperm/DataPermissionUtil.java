package com.kjlink.cloud.mybatis.dataperm;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import com.kjlink.cloud.mybatis.annotation.DataPermission;

/**
 * 框架内部缓存
 *
 * @author Fulai
 * @since 2025-07-21
 */
public class DataPermissionUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DataPermissionUtil.class);
    private static final ThreadLocal<DataPermissionConfig> LOCAL_HANDLER = new ThreadLocal<>();
    //id反射注解缓存
    private static Map<String, DataPermissionConfig> annotationCacheMap =
            new ConcurrentHashMap<>();
    private static final DataPermissionConfig NULL_OBJECT = new DataPermissionConfig("", "");

    public record DataPermissionConfig(String table, String sql) {}

    /**
     * 数据权限设置到线程上，只能管一次mapper方法调用
     *
     * @param sql 数据权限sql片段
     */
    public static void enable(String tableName, String sql) {
        LOCAL_HANDLER.set(new DataPermissionConfig(tableName, sql));
    }

    @Nullable
    public static DataPermissionConfig getAndRemove(String mappedStatementId) {
        DataPermissionConfig config = LOCAL_HANDLER.get();
        if (config != null) {
            LOCAL_HANDLER.remove();
            return config;
        }
        //反射注解
        config = annotationCacheMap.computeIfAbsent(mappedStatementId,
                DataPermissionUtil::findDataPermissionAnnotation);
        if (config == NULL_OBJECT) {
            return null;
        }
        return config;
    }

    private static DataPermissionConfig findDataPermissionAnnotation(String mappedStatementId) {
        //第一次进入
        try {
            int lastDot = mappedStatementId.lastIndexOf(StringPool.DOT);
            String className = mappedStatementId.substring(0, lastDot);
            String methodName = mappedStatementId.substring(lastDot + 1);
            Class<?> clazz = Class.forName(className);
            Method method = ReflectUtil.getMethodByName(clazz, methodName);
            DataPermission annotation = method.getAnnotation(DataPermission.class);
            if (annotation != null) {
                return new DataPermissionConfig(annotation.table(), annotation.value());
            }
        } catch (Exception e) {
            LOG.error("反射@DataPermission异常", e);
        }
        return NULL_OBJECT;
    }
}
