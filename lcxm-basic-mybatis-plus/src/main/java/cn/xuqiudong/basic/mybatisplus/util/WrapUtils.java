package cn.xuqiudong.basic.mybatisplus.util;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.mybatisplus.builder.WrapperBuilder;
import cn.xuqiudong.basic.mybatisplus.query.Column;
import cn.xuqiudong.basic.mybatisplus.query.MpQuery;
import cn.xuqiudong.basic.mybatisplus.query.OrderBy;
import cn.xuqiudong.basic.mybatisplus.query.Where;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 描述:
 * 封装查询条件  QueryWrapper 的 工具类
 *
 * @author Vic.xu
 * @since 2025-11-06 16:29
 */
public class WrapUtils {

    /**
     * 创建查询条件
     */
    public static <T, R> QueryWrapper<T> createWrapper(Column<T, R> column, R value) {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        queryWrapper.eq(isNotEmpty(value), ColumnUtils.safeColumn(column), value);
        return queryWrapper;
    }

    /**
     * 创建查询条件
     */
    public static <T> QueryWrapper<T> createWrapper(MpQuery query) {
        return QueryConditionUtils.createWrapper(query);
    }

    /**
     * 创建查询条件
     */
    public static <T> Where<T> createWhere(MpQuery query) {
        WrapperBuilder builder = QueryConditionUtils.builder(query);
        return builder.toWhere();
    }


    private static boolean isNotEmpty(Object value) {
        if (value instanceof CharSequence) {
            return StrUtil.isNotEmpty((CharSequence) value);
        }
        return value != null;
    }

    /**
     * 为查询设置排序
     */
    public static <T> QueryWrapper<T> setOrderBy(QueryWrapper<T> queryWrapper, OrderBy orderBy) {
        if (orderBy == null || queryWrapper == null) {
            return queryWrapper;
        }
        List<OrderBy.OrderColumn> orderColumns = orderBy.getOrderColumns();
        for (OrderBy.OrderColumn orderColumn : orderColumns) {
            if (orderColumn.getOrderType() == OrderBy.OrderType.ASC) {
                queryWrapper.orderByAsc(orderColumn.getColumn());
            } else {
                queryWrapper.orderByDesc(orderColumn.getColumn());
            }
        }
        return queryWrapper;
    }

    /**
     * 为分页设置排序
     */
    public static <T> void setOrderBy(Page<T> page, OrderBy orderBy) {
        if (orderBy == null) {
            return;
        }
        List<OrderBy.OrderColumn> orderColumns = orderBy.getOrderColumns();
        List<OrderItem> orders = new ArrayList<>();
        for (OrderBy.OrderColumn orderColumn : orderColumns) {
            if (orderColumn.getOrderType() == OrderBy.OrderType.ASC) {
                orders.add(OrderItem.asc(orderColumn.getColumn()));
            } else {
                orders.add(OrderItem.desc(orderColumn.getColumn()));
            }
        }
        page.setOrders(orders);
    }

    /**
     * 创建in查询条件
     */
    public static <T, R> QueryWrapper<T> columnIn(Column<T, R> column, Collection<R> values){
        QueryWrapper<T> queryWrapper = Wrappers.query();
        queryWrapper.in(ColumnUtils.safeColumn(column), values);
        return queryWrapper;
    }
}
