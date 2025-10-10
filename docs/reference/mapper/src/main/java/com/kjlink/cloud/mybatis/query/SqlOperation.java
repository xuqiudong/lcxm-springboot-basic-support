package com.kjlink.cloud.mybatis.query;

/**
 * <b><code>sql语句的条件枚举值</code></b>
 * <p/>
 * Description
 * <p/>
 * <b>Creation Time:</b>2023/1/11 18:01.
 *
 * @author zhangjc
 * @since system 1.0
 */
public enum SqlOperation {
    /**
     *
     */
    AUTO,
    /**
     * 等于
     */
    EQ,
    /**
     * 模糊查询，%_%
     */
    LIKE,
    /**
     * 左模糊查询，%_
     */
    LIKE_LEFT,
    /**
     * 右模糊查询，_%
     */
    LIKE_RIGHT,
    /**
     * 不区分大小写的like
     * PostgreSQL：	ILIKE '%abc%
     * MySQL：	LOWER(col) LIKE LOWER('%abc%')
     * Oracle：	LOWER(col) LIKE LOWER('%abc%')
     */
    ILIKE,
    /**
     * 小于
     */
    LT,
    /**
     * 大于
     */
    GT,
    /**
     * 小于等于
     */
    LE,
    /**
     * 大于等于
     */
    GE,
    /**
     * 在。。。之中
     */
    IN,
    /**
     * 不在。。。之中
     */
    NOT_IN,
    /**
     * 不等于
     */
    NE,
    /**
     * 介于，字段类型必须是数组
     */
    BETWEEN,
    NOT_LIKE,
    IS_NULL,
    IS_NOT_NULL;
}
