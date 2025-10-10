package com.kjlink.cloud.mybatis.interceptor;

import org.apache.ibatis.type.JdbcType;

public class ColumnValue {
    private String name;
    private JdbcType jdbcType;
    private Object value;

    public ColumnValue(String name, JdbcType jdbcType, Object value) {
        this.name = name;
        this.jdbcType = jdbcType;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public Object getValue() {
        return value;
    }
}
