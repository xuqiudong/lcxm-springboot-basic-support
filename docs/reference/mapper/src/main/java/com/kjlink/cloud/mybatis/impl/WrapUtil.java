package com.kjlink.cloud.mybatis.impl;

import java.util.Collection;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.kjlink.cloud.mybatis.query.Column;
import com.kjlink.cloud.mybatis.query.OrderBy;
import com.kjlink.cloud.mybatis.query.QueryUtil;
import com.kjlink.cloud.mybatis.query.Where;

import static com.kjlink.cloud.mybatis.query.ColumnUtil.safeColumn;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2024-01-15
 */
public class WrapUtil {

    public static <T, A> QueryWrapper<T> createQueryWrapper(Column<T, A> column1, A value1) {
        return createQueryWrapper(column1, value1, null, null, null, null);
    }

    public static <T, A, B> QueryWrapper<T> createQueryWrapper(Column<T, A> column1, A value1, Column<T, B> column2,
            B value2) {
        return createQueryWrapper(column1, value1, column2, value2, null, null);
    }

    public static <T, A, B, C> QueryWrapper<T> createQueryWrapper(Column<T, A> column1, A value1, Column<T, B> column2,
            B value2,
            Column<T, C> column3, C value3) {
        QueryWrapper<T> wrapper = Wrappers.query();
        if (isNotEmpty(value1)) {
            wrapper.eq(safeColumn(column1), value1);
        }
        if (isNotEmpty(value2)) {
            wrapper.eq(safeColumn(column2), value2);
        }
        if (isNotEmpty(value3)) {
            wrapper.eq(safeColumn(column3), value3);
        }
        return wrapper;
    }

    private static boolean isNotEmpty(Object value) {
        if (value instanceof CharSequence cs) {
            return StrUtil.isNotEmpty(cs);
        }
        return value != null;
    }


    public static <T> QueryWrapper<T> createQueryWrapper(Where where) {
        QueryWrapper<T> query = Wrappers.query();
        if (where == null) {
            return query;
        }
        where.apply(query);
        return query;
    }

    public static <T> QueryWrapper<T> setOrderBy(QueryWrapper<T> wrapper, OrderBy orderBy) {
        //设置排序信息
        if (orderBy != null) {
            Map<String, Boolean> columnOrders = orderBy.getColumnOrders();
            for (Map.Entry<String, Boolean> entry : columnOrders.entrySet()) {
                if (entry.getValue()) {
                    wrapper.orderByAsc(entry.getKey());
                } else {
                    wrapper.orderByDesc(entry.getKey());
                }
            }
        }
        return wrapper;
    }

    public static <T, A> QueryWrapper<T> columnIn(Column<T, A> column, Collection<A> inValues) {
        QueryWrapper<T> wrapper = Wrappers.query();
        wrapper.in(safeColumn(column), inValues);
        return wrapper;
    }

    public static <T> QueryWrapper<T> build(Object queryVo) {
        Where where = QueryUtil.build(queryVo);
        return createQueryWrapper(where);
    }

    public static <T, A, B, C> UpdateWrapper<T> updateSet(Column<T, A> column1, A value1, Column<T, B> column2,
            B value2, Column<T, C> column3, C value3) {
        UpdateWrapper<T> wrapper = Wrappers.update();
        if (column1 != null) {
            wrapper.set(safeColumn(column1), value1);
        }
        if (column2 != null) {
            wrapper.set(safeColumn(column2), value2);
        }
        if (column3 != null) {
            wrapper.set(safeColumn(column3), value3);
        }
        return wrapper;
    }
}
