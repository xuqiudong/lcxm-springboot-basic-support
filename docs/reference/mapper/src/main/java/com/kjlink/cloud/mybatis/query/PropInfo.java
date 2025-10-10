package com.kjlink.cloud.mybatis.query;

import cn.hutool.core.bean.PropDesc;

/**
 * 缓存QueryVo的属性信息
 *
 * @author Fulai
 * @since 2024-01-22
 */
class PropInfo {
    private String[] columns;
    private SqlOperation op;
    private boolean queryNull;
    private boolean trim;
    private boolean ignore; //ignore或transient
    private String splitter;
    //字段类型
    private Class<?> fieldType;
    private PropDesc prop;

    PropInfo(PropDesc prop) {
        this.prop = prop;
    }

    Object getValue(Object query) {
        return prop.getValue(query);
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public SqlOperation getOp() {
        return op;
    }

    public void setOp(SqlOperation op) {
        this.op = op;
    }

    public boolean isQueryNull() {
        return queryNull;
    }

    public void setQueryNull(boolean queryNull) {
        this.queryNull = queryNull;
    }

    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public String getSplitter() {
        return splitter;
    }

    public void setSplitter(String splitter) {
        this.splitter = splitter;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public PropDesc getProp() {
        return prop;
    }

    public void setProp(PropDesc prop) {
        this.prop = prop;
    }
}
