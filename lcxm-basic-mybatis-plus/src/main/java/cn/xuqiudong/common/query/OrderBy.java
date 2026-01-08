package cn.xuqiudong.common.query;

import cn.xuqiudong.common.util.ColumnUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * 描述:
 * 构建排序字段
 * 可以在xml 或@select 中直接使用:
 * <code>
 * mapper:
 * select(OrderBy orderBy)
 * xml:
 * select * from table_name where 1=1  ${oderBy}
 * </code>
 *
 * @author Vic.xu
 * @since 2025-10-29 13:27
 */
public class OrderBy {

    private final List<OrderColumn> orderColumns = new ArrayList<>();


    /**
     * 转化为 排序sql， 并且 拼接上  ORDER BY
     */
    @Override
    public String toString() {
        if (CollectionUtils.isEmpty(orderColumns)) {
            return "";
        }
        return " ORDER BY " + toOrderSql();
    }

    /**
     * 转为排序字符串: column1 desc, column2 asc, ....
     */
    public String toOrderSql() {
        StringJoiner joiner = new StringJoiner(", ");
        for (OrderColumn orderColumn : orderColumns) {
            joiner.add(orderColumn.toOrderSql());
        }
        return joiner.toString();
    }

    /**
     * 构建降序排序字段
     */
    public static OrderBy desc(String column) {
        return new OrderBy().thenDesc(column);
    }

    /**
     * 构建降序排序字段
     */
    public static <T, R> OrderBy desc(Column<T, R> column) {
        return new OrderBy().thenDesc(column);
    }


    /**
     * 构建升序排序字段
     */
    public static OrderBy asc(String column) {
        return new OrderBy().thenAsc(column);
    }

    /**
     * 构建升序排序字段
     */
    public static <T, R> OrderBy asc(Column<T, R> column) {
        return new OrderBy().thenAsc(column);
    }

    /**
     * 添加降序排序字段
     */
    public OrderBy thenDesc(String column) {
        String safeColumn = ColumnUtils.safeColumn(column);
        orderColumns.add(OrderColumn.desc(safeColumn));
        return this;
    }

    /**
     * 添加降序排序字段
     */
    public <T, R> OrderBy thenDesc(Column<T, R> column) {
        String safeColumn = ColumnUtils.safeColumn(column);
        return this.thenDesc(safeColumn);
    }

    /**
     * 添加升序排序字段
     */
    public OrderBy thenAsc(String column) {
        String safeColumn = ColumnUtils.safeColumn(column);
        orderColumns.add(OrderColumn.asc(safeColumn));
        return this;
    }

    /**
     * 添加升序排序字段
     */
    public <T, R> OrderBy thenAsc(Column<T, R> column) {
        String safeColumn = ColumnUtils.safeColumn(column);
        return this.thenAsc(safeColumn);
    }


    public List<OrderColumn> getOrderColumns() {
        return Collections.unmodifiableList(orderColumns);
    }


    /**
     * 排序字段
     */
    public static class OrderColumn {

        public String column;

        public OrderType orderType;

        /**
         * 构建降序排序字段
         */
        public static OrderColumn desc(String column) {
            return new OrderColumn(column, OrderType.DESC);
        }

        /**
         * 构建升序排序字段
         */
        public static OrderColumn asc(String column) {
            return new OrderColumn(column, OrderType.ASC);
        }

        /**
         * 转为排序字符串
         */
        public String toOrderSql() {
            return column + " " + orderType.name().toLowerCase();
        }

        public OrderColumn(String column, OrderType orderType) {
            this.column = column;
            this.orderType = orderType;
        }


    }


    /**
     * 排序类型
     */
    public enum OrderType {
        ASC,
        DESC
    }
}
