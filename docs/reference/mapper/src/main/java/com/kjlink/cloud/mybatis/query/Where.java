package com.kjlink.cloud.mybatis.query;

import java.util.Arrays;
import java.util.Collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import org.springframework.lang.Nullable;

import static com.kjlink.cloud.mybatis.query.ColumnUtil.safeColumn;

/**
 * <h1>Where条件构建工具</h1>
 * 简化的MBP中Wrapper的复杂用法，
 * 仅用于构建where条件，可用在select,update,delete方法中。
 * <p>
 * where对象也可以直接用在Mapper接口上，示例：
 * <code><pre>
 *  Mapper接口：
 *  List selectByWhere(Where where);
 *  XML:
 * `select xxx from table ${where}`
 *  </pre></code>
 * 搭配QueryUtil使用，可以实现复杂的where条件拼装
 * </p>
 *
 * @author Fulai
 * @since 2023-03-31
 */
public class Where extends WhereBuilder {
    /**
     * 空的where条件
     *
     * @return
     */
    public static Where create() {
        return new Where();
    }

    /**
     * 从query对象创建where条件
     *
     * @param queryObj 查询对象，参见QueryUtil
     * @return where条件
     */
    public static Where create(Object queryObj) {
        Where where = new Where();
        QueryUtil.buildInternal(where, queryObj);
        return where;
    }

    /**
     * @return
     * @deprecated 使用eql
     */
    @Deprecated
    public static <T, A> Where equal(Column<T, A> column, A value) {
        Where builder = new Where();
        return builder.eq(column, value);
    }

    /**
     * 创建where实例，并增加eq条件
     *
     * @param column 字段名
     * @param value  值
     * @param <T>    任意实体
     * @param <A>    字段类型
     * @return where实例
     */
    public static <T, A> Where equ(Column<T, A> column, A value) {
        Where builder = new Where();
        return builder.eq(column, value);
    }

    /**
     * equal，忽略null值
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    public <T, A> Where eq(Column<T, A> column, @Nullable A value) {
        return eq(safeColumn(column), value);
    }

    /**
     * 增加=条件
     *
     * @param aliasColumn 带别名的列，例如t.abc = 1
     * @param value
     * @return
     */
    public Where eq(String aliasColumn, @Nullable Object value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(aliasColumn, SqlOperation.EQ, value);
        return this;
    }

    /**
     * eq，允许null值生成is null
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    public <T, A> Where eqNull(Column<T, A> column, @Nullable A value) {
        return eqNull(safeColumn(column), value);
    }

    public Where eqNull(String aliasColumn, @Nullable Object value) {
        if (value == null) {
            return isNull(aliasColumn); //生成isNull
        }
        and(aliasColumn, SqlOperation.EQ, value);
        return this;
    }

    /**
     * not equal，忽略null值
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    public <T, A> Where ne(Column<T, A> column, @Nullable A value) {
        return ne(safeColumn(column), value);
    }

    public Where ne(String aliasColumn, @Nullable Object value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(aliasColumn, SqlOperation.NE, value);
        return this;
    }

    /**
     * great_than，忽略null值
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    public <T, A> Where gt(Column<T, A> column, @Nullable A value) {
        return gt(safeColumn(column), value);
    }

    public Where gt(String aliasColumn, @Nullable Object value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(aliasColumn, SqlOperation.GT, value);
        return this;
    }

    /**
     * great_equal，忽略null值
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    public <T, A> Where ge(Column<T, A> column, @Nullable A value) {
        return ge(safeColumn(column), value);
    }

    public Where ge(String aliasColumn, @Nullable Object value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(aliasColumn, SqlOperation.GE, value);
        return this;
    }

    /**
     * little than，忽略null值
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    public <T, A> Where lt(Column<T, A> column, @Nullable A value) {
        return lt(safeColumn(column), value);
    }

    public Where lt(String aliasColumn, @Nullable Object value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(aliasColumn, SqlOperation.LT, value);
        return this;
    }

    /**
     * little_or_equal小于等于
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    public <T, A> Where le(Column<T, A> column, @Nullable A value) {
        return le(safeColumn(column), value);
    }

    public Where le(String aliasColumn, @Nullable Object value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(aliasColumn, SqlOperation.LE, value);
        return this;
    }

    /**
     * 介于
     *
     * @param column
     * @param lowValue  为空时仅生成<=
     * @param highValue 为空时仅生成>=
     * @param <A>
     * @return
     */
    public <T, A> Where between(Column<T, A> column, @Nullable A lowValue, @Nullable A highValue) {
        return between(safeColumn(column), lowValue, highValue);
    }

    public Where between(String aliasColumn, @Nullable Object lowValue, @Nullable Object highValue) {
        if (lowValue == null && highValue == null) {
            return this; //忽略null值
        }

        if (lowValue != null && highValue != null) {
            and(aliasColumn, SqlOperation.BETWEEN, new Object[]{lowValue, highValue});
            return this;
        }

        if (lowValue != null) {
            return ge(aliasColumn, lowValue);
        }
        return le(aliasColumn, highValue);
    }

    /**
     * like %值%，忽略空值
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @return
     */
    public <T> Where like(Column<T, String> column, @Nullable String value) {
        return like(safeColumn(column), value);
    }

    public Where like(String aliasColumn, @Nullable String value) {
        Object trimValue = trimToNull(value);
        if (trimValue != null) {
            and(aliasColumn, SqlOperation.LIKE, trimValue);
        }
        return this;
    }

    /**
     * ilike %值%，忽略空值
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @return
     */
    public <T> Where ilike(Column<T, String> column, @Nullable String value) {
        return ilike(safeColumn(column), value);
    }

    public Where ilike(String aliasColumn, @Nullable String value) {
        Object trimValue = trimToNull(value);
        if (trimValue != null) {
            and(aliasColumn, SqlOperation.ILIKE, trimValue);
        }
        return this;
    }

    /**
     * not like %值%
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @return
     */
    public <T> Where notLike(Column<T, String> column, @Nullable String value) {
        return notLike(safeColumn(column), value);
    }

    public Where notLike(String aliasColumn, @Nullable String value) {
        Object trimValue = trimToNull(value);
        if (trimValue != null) {
            and(aliasColumn, SqlOperation.NOT_LIKE, value);
        }
        return this;
    }

    /**
     * like %值
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @return
     */
    public <T> Where likeLeft(Column<T, String> column, @Nullable String value) {
        return likeLeft(safeColumn(column), value);
    }

    public Where likeLeft(String aliasColumn, @Nullable String value) {
        Object trimValue = trimToNull(value);
        if (trimValue != null) {
            and(aliasColumn, SqlOperation.LIKE_LEFT, trimValue);
        }
        return this;
    }

    /**
     * like 值$
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @return
     */
    public <T> Where likeRight(Column<T, String> column, @Nullable String value) {
        return likeRight(safeColumn(column), value);
    }

    public Where likeRight(String aliasColumn, @Nullable String value) {
        Object trimValue = trimToNull(value);
        if (trimValue != null) {
            and(aliasColumn, SqlOperation.LIKE_RIGHT, trimValue);
        }
        return this;
    }

    /**
     * and xxx is null
     *
     * @param column
     * @param <T>
     * @param <A>
     * @return
     */
    public <T, A> Where isNull(Column<T, A> column) {
        return isNull(safeColumn(column));
    }

    public Where isNull(String aliasColumn) {
        and(aliasColumn, SqlOperation.IS_NULL, null);
        return this;
    }

    /**
     * and xxx is not null
     *
     * @param column
     * @param <T>
     * @param <A>
     * @return
     */
    public <T, A> Where isNotNull(Column<T, A> column) {
        return isNotNull(safeColumn(column));
    }

    public Where isNotNull(String aliasColumn) {
        and(aliasColumn, SqlOperation.IS_NOT_NULL, null);
        return this;
    }

    public <T, A> Where in(Column<T, A> column, @Nullable A[] values) {
        if (ArrayUtil.isEmpty(values)) {
            return this;
        }
        return in(safeColumn(column), Arrays.asList(values));
    }

    public <A> Where in(String aliasColumn, @Nullable A[] values) {
        if (ArrayUtil.isNotEmpty(values)) {
            and(aliasColumn, SqlOperation.IN, Arrays.asList(values));
        }
        return this;
    }

    public <T, A> Where in(Column<T, A> column, @Nullable Collection<A> values) {
        return in(safeColumn(column), values);
    }

    public Where in(String aliasColumn, @Nullable Collection<?> values) {
        if (CollUtil.isNotEmpty(values)) {
            and(aliasColumn, SqlOperation.IN, values);
        }
        return this;
    }

    public <T, A> Where notIn(Column<T, A> column, @Nullable A[] values) {
        if (ArrayUtil.isNotEmpty(values)) {
            notIn(safeColumn(column), values);
        }
        return this;
    }

    public <A> Where notIn(String aliasColumn, @Nullable A[] values) {
        if (ArrayUtil.isNotEmpty(values)) {
            and(aliasColumn, SqlOperation.NOT_IN, Arrays.asList(values));
        }
        return this;
    }

    public <T, A> Where notIn(Column<T, A> column, @Nullable Collection<A> values) {
        return notIn(safeColumn(column), values);
    }

    public <A> Where notIn(String aliasColumn, @Nullable Collection<A> values) {
        if (CollUtil.isNotEmpty(values)) {
            and(aliasColumn, SqlOperation.NOT_IN, values);
        }
        return this;
    }
}
