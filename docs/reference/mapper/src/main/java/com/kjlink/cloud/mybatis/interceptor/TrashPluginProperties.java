package com.kjlink.cloud.mybatis.interceptor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 回收站插件配置
 *
 * @author Fulai
 * @since 2025-08-25
 */
@ConfigurationProperties(prefix = "mybatis-plus.trash-plugin")
public class TrashPluginProperties {
    //回收站主表名称(推荐sys_trash）
    private String table;
    //最大支持的LOB大小：16MB，防止内存溢出, 也是Mysql的MEDIUMBLOB, MEDIUMTEXT类型最大长度
    private long maxLength = 16777216L;
    //指定要包含的表名称前缀(不配置或为"ALL"时包含所有表）
    private String[] includes;
    //指定要排除的表名称前缀（先包含再排除）
    private String[] excludes;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(long maxLength) {
        this.maxLength = maxLength;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }
}
