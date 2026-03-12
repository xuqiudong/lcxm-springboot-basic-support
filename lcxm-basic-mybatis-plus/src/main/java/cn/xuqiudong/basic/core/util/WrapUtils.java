package cn.xuqiudong.basic.core.util;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.core.builder.WrapperBuilder;
import cn.xuqiudong.basic.core.query.Column;
import cn.xuqiudong.basic.core.query.MpQuery;
import cn.xuqiudong.basic.core.query.OrderBy;
import cn.xuqiudong.basic.core.query.Where;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *    封装查询条件  QueryWrapper 的 工具类
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
    public static <T> void setOrderBy(QueryWrapper<T> queryWrapper, OrderBy orderBy) {
        if (orderBy == null) {
            return;
        }
        List<OrderBy.OrderColumn> orderColumns = orderBy.getOrderColumns();
        for (OrderBy.OrderColumn orderColumn : orderColumns) {
            if (orderColumn.orderType == OrderBy.OrderType.ASC) {
                queryWrapper.orderByAsc(orderColumn.column);
            } else {
                queryWrapper.orderByDesc(orderColumn.column);
            }
        }
    }

    /**
     * 为分页设置排序
     */
    public static <T> void setOrderBy(Page<T> page, OrderBy orderBy){
        if (orderBy == null) {
            return;
        }
        List<OrderBy.OrderColumn> orderColumns = orderBy.getOrderColumns();
        List<OrderItem> orders = new ArrayList<>();
        for (OrderBy.OrderColumn orderColumn : orderColumns) {
            if (orderColumn.orderType == OrderBy.OrderType.ASC) {
                orders.add(OrderItem.asc(orderColumn.column));
            } else {
                orders.add(OrderItem.desc(orderColumn.column));
            }
        }
        page.setOrders(orders);
    }
}
