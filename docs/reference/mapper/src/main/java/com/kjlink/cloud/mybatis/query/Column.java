package com.kjlink.cloud.mybatis.query;

import java.io.Serializable;

/**
 * 接收pojo类的setter lambda表达式，例如
 * BaseEntity::setId
 * 主要是防止写死字段名称
 *
 * @author kj
 * @since 2023-02-07
 */
@FunctionalInterface
public interface Column<T, V> extends Serializable {
    void accept(T entity, V value);
}
