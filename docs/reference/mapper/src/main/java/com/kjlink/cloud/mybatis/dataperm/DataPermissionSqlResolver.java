package com.kjlink.cloud.mybatis.dataperm;

import org.springframework.lang.Nullable;

/**
 * 解析SQL数据权限
 *
 * @author Fulai
 * @since 2025-07-21
 */
public interface DataPermissionSqlResolver {
    /**
     * 根据表名和原始SQL动态替换占位符号
     *
     * @param table 表名，注意兼容大小写
     * @param alias 表别名，可能为null
     * @param sqlFragment   注解上配置的SQL过滤条件模板
     * @return 替换后的SQL片段，用于拼接到where或join条件
     */
    String resolve(String table, @Nullable String alias, String sqlFragment);
}
