package com.kjlink.cloud.mybatis.meta;

import org.apache.ibatis.reflection.MetaObject;

/**
 * 用于增加自定义的注入字段
 * MBP官方仅支持原值为null时填充，这里不做限制
 *
 * @author Fulai
 * @since 2023-04-10
 */
public interface MetaPropertyHandler {

    /**
     * 执行insert前注入属性
     *
     * @param metaObject
     */
    void insertFill(MetaObject metaObject);

    /**
     * 执行update前注入属性
     *
     * @param metaObject
     */
    void updateFill(MetaObject metaObject);
}
