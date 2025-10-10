package com.kjlink.cloud.mybatis.query;

import cn.hutool.core.util.StrUtil;

/**
 * ilike语句构建工具
 *
 * @author Fulai
 * @since 2025-06-04
 */
public interface ILikeBuilder {
    /**
     * 传入列名称和字符串值，返回完整的ilike子句
     *
     * @param column 列名称
     * @return
     */
    String ilike(String column);

    ILikeBuilder ORACLE = (column) -> StrUtil.format("LOWER({}) LIKE LOWER({0})", column);

    ILikeBuilder MYSQL = (column) -> StrUtil.format("LOWER({}) LIKE LOWER({0})", column);

    ILikeBuilder POSTGRE_SQL = (column) -> StrUtil.format("{} ILIKE {0}", column);
}
