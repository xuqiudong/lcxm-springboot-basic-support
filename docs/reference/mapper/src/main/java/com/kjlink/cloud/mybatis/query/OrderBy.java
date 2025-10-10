package com.kjlink.cloud.mybatis.query;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;


/**
 * <h1>Order By语句构建工具</h1>
 * 使用lambda构建排序字段
 * 可以直接用在Mapper接口上，用于拼接Order by语句
 * <code><pre>
 *  Mapper接口：
 *  List selectByWhere(Where&lt;T&gt; where, OrderBy orderBy);
 *  XML:
 * `select xxx from table ${where} ${orderBy}`
 *  </pre></code>
 *
 * @author Fulai
 * @since 2023-03-17
 */
public class OrderBy {
    private final Map<String, Boolean> columnOrders = new LinkedHashMap<>();

    /**
     * 按列正排
     * @param column
     * @return
     * @param <T>
     * @param <V>
     */
    public static <T, V> OrderBy asc(Column<T, V> column) {
        return new OrderBy().thenAsc(column);
    }

    /**
     * 按列倒排
     * @param column
     * @return
     * @param <T>
     * @param <V>
     */
    public static <T, V> OrderBy desc(Column<T, V> column) {
        return new OrderBy().thenDesc(column);
    }

    /**
     * 用于带别名的列名称，不带别名时请使用lambda表达式
     * @param aliasColumn
     * @return
     */
    public static OrderBy asc(String aliasColumn) {
        return new OrderBy().thenAsc(aliasColumn);
    }

    /**
     * 用于带别名的列名称，不带别名时请使用lambda表达式
     * @param aliasColumn
     * @return
     */
    public static OrderBy desc(String aliasColumn) {
        return new OrderBy().thenDesc(aliasColumn);
    }

    public <T, V> OrderBy thenAsc(Column<T, V> column) {
        String safeColumn = ColumnUtil.safeColumn(column);
        columnOrders.put(safeColumn, true);
        return this;
    }

    public <T, V> OrderBy thenDesc(Column<T, V> column) {
        String safeColumn = ColumnUtil.safeColumn(column);
        columnOrders.put(safeColumn, false);
        return this;
    }


    public OrderBy thenAsc(String aliasColumn) {
        String safeColumn = ColumnUtil.safeColumn(aliasColumn);
        columnOrders.put(safeColumn, true);
        return this;
    }

    public OrderBy thenDesc(String aliasColumn) {
        String safeColumn = ColumnUtil.safeColumn(aliasColumn);
        columnOrders.put(safeColumn, false);
        return this;
    }

    /**
     * 框架内部使用
     *
     * @param column
     * @param asc
     */
    public void internalSet(String column, boolean asc) {
        String safeColumn = ColumnUtil.safeColumn(column);
        columnOrders.put(safeColumn, asc);
    }

    public Map<String, Boolean> getColumnOrders() {
        return Collections.unmodifiableMap(columnOrders);
    }

    /**
     * mybatis自动调用，生成sql语言
     *
     * @return
     */
    @Override
    public String toString() {
        if (CollUtil.isEmpty(columnOrders)) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        sql.append("ORDER BY ");
        int index = 0;
        for (Map.Entry<String, Boolean> entry : columnOrders.entrySet()) {
            if (index > 0) {
                sql.append(", ");
            }
            sql.append(entry.getKey());
            if (entry.getValue()) {
                sql.append(" ASC");
            } else {
                sql.append(" DESC");
            }
            index++;
        }
        return sql.toString();
    }
}
