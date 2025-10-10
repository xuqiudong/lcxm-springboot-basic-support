package com.kjlink.cloud.mybatis.query;

import java.util.Arrays;
import java.util.Collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import org.springframework.lang.Nullable;

/**
 * <h1>Where条件构建工具，支持设置表别名</h1>
 * 简化的MBP中Wrapper的复杂用法，
 * 仅用于构建where条件，可用在select,update,delete方法中。
 * <p>
 * where对象也可以直接用在Mapper接口上，示例：
 * <code><pre>
 *  Mapper接口：
 *  List selectList(Wheres where);
 *  XML:
 * `select xxx from table ${where}`
 *  </pre></code>
 * 搭配QueryUtil使用，可以实现复杂的where条件拼装
 * </p>
 *
 * @author Fulai
 * @since 2023-03-31
 */
@Deprecated
public class Wheres extends WhereBuilder {
    private Wheres() {
        //private
    }

    /**
     * 空的where条件
     *
     * @param <T>
     * @return
     */
    public static <T> Wheres create() {
        return new Wheres();
    }

    public static <T> Wheres create(Object queryObj) {
        Wheres where = new Wheres();
        QueryUtil.buildInternal(where, queryObj);
        return where;
    }

    /**
     * 从equals开始构建where查询
     *
     * @param column
     * @param value
     * @param <T>
     * @param <A>
     * @return
     */
    public static <T, A> Wheres equal(char tableAlias, Column<T, A> column, A value) {
        Wheres builder = new Wheres();
        return builder.eq(tableAlias, column, value);
    }

    /**
     * equal，忽略null值
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    public <T, A> Wheres eq(char tableAlias, Column<T, A> column, @Nullable A value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(tableAlias, column, SqlOperation.EQ, value);
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
    public <T, A> Wheres eqNull(char tableAlias, Column<T, A> column, @Nullable A value) {
        if (value == null) {
            return isNull(tableAlias, column); //生成isNull
        }
        and(tableAlias, column, SqlOperation.EQ, value);
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
    public <T, A> Wheres ne(char tableAlias, Column<T, A> column, @Nullable A value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(tableAlias, column, SqlOperation.NE, value);
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
    public <T, A> Wheres gt(char tableAlias, Column<T, A> column, @Nullable A value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(tableAlias, column, SqlOperation.GT, value);
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
    public <T, A> Wheres ge(char tableAlias, Column<T, A> column, @Nullable A value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(tableAlias, column, SqlOperation.GE, value);
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
    public <T, A> Wheres lt(char tableAlias, Column<T, A> column, @Nullable A value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(tableAlias, column, SqlOperation.LT, value);
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
    public <T, A> Wheres le(char tableAlias, Column<T, A> column, @Nullable A value) {
        if (value == null) {
            return this; //忽略null值
        }
        and(tableAlias, column, SqlOperation.LE, value);
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
    public <T, A> Wheres between(char tableAlias, Column<T, A> column, @Nullable A lowValue, @Nullable A highValue) {
        if (lowValue == null && highValue == null) {
            return this; //忽略null值
        }

        if (lowValue != null && highValue != null) {
            and(tableAlias, column, SqlOperation.BETWEEN, new Object[]{lowValue, highValue});
            return this;
        }

        if (lowValue != null) {
            return ge(tableAlias, column, lowValue);
        }
        return le(tableAlias, column, highValue);
    }

    /**
     * like %值%，忽略空值
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @param <A>
     * @return
     */
    public <T, A> Wheres like(char tableAlias, Column<T, A> column, @Nullable A value) {
        A trimValue = trimToNull(value);
        if (trimValue != null) {
            and(tableAlias, column, SqlOperation.LIKE, trimValue);
        }
        return this;
    }

    /**
     * not like %值%
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @param <A>
     * @return
     */
    public <T, A> Wheres notLike(char tableAlias, Column<T, A> column, @Nullable A value) {
        A trimValue = trimToNull(value);
        if (trimValue != null) {
            and(tableAlias, column, SqlOperation.NOT_LIKE, value);
        }
        return this;
    }

    /**
     * like %值
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @param <A>
     * @return
     */
    public <T, A> Wheres likeLeft(char tableAlias, Column<T, A> column, @Nullable A value) {
        A trimValue = trimToNull(value);
        if (trimValue != null) {
            and(tableAlias, column, SqlOperation.LIKE_LEFT, trimValue);
        }
        return this;
    }

    /**
     * like 值$
     *
     * @param column
     * @param value  自动去空格,trimToNull
     * @param <A>
     * @return
     */
    public <T, A> Wheres likeRight(char tableAlias, Column<T, A> column, @Nullable A value) {
        A trimValue = trimToNull(value);
        if (trimValue != null) {
            and(tableAlias, column, SqlOperation.LIKE_RIGHT, trimValue);
        }
        return this;
    }

    public <T, A> Wheres isNull(char tableAlias, Column<T, A> column) {
        and(tableAlias, column, SqlOperation.IS_NULL, null);
        return this;
    }

    public <T, A> Wheres isNotNull(char tableAlias, Column<T, A> column) {
        and(tableAlias, column, SqlOperation.IS_NOT_NULL, null);
        return this;
    }

    public <T, A> Wheres in(char tableAlias, Column<T, A> column, @Nullable A[] values) {
        if (ArrayUtil.isNotEmpty(values)) {
            and(tableAlias, column, SqlOperation.IN, Arrays.asList(values));
        }
        return this;
    }

    public <T, A> Wheres in(char tableAlias, Column<T, A> column, @Nullable Collection<A> values) {
        if (CollUtil.isNotEmpty(values)) {
            and(tableAlias, column, SqlOperation.IN, values);
        }
        return this;
    }

    public <T, A> Wheres notIn(char tableAlias, Column<T, A> column, @Nullable A[] values) {
        if (ArrayUtil.isNotEmpty(values)) {
            and(tableAlias, column, SqlOperation.NOT_IN, Arrays.asList(values));
        }
        return this;
    }

    public <T, A> Wheres notIn(char tableAlias, Column<T, A> column, @Nullable Collection<A> values) {
        if (CollUtil.isNotEmpty(values)) {
            and(tableAlias, column, SqlOperation.NOT_IN, values);
        }
        return this;
    }

    /**
     * 拼接and语句，仅供框架内部使用
     *
     * @param tableAlias 表别名，例如x, t
     * @param column
     * @param op
     * @param value
     * @param <A>
     */
    public <T, A> void and(char tableAlias, Column<T, A> column, SqlOperation op, Object value) {
        String colName = ColumnUtil.safeColumn(tableAlias, column);
        super.and(colName, op, value);
    }
}
