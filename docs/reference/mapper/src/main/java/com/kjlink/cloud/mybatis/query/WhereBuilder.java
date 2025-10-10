package com.kjlink.cloud.mybatis.query;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

/**
 * Where语句构建工具
 *
 * @author Fulai
 * @since 2025-04-30
 */
class WhereBuilder {
    private final List<Condition> conditions = new LinkedList<>();
    private QueryWrapper queryWrapper;
    private static ILikeBuilder globalIlikeBuilder;
    private ILikeBuilder iLikeBuilder;

    public void setILikeBuilder(ILikeBuilder iLikeBuilder) {
        this.iLikeBuilder = iLikeBuilder;
    }

    private String buildILike(String column, Object value) {
        if (iLikeBuilder != null) {
            return iLikeBuilder.ilike(column);
        }
        if (globalIlikeBuilder == null) {
            //需要根据数据库类型在spring中注册一个全局的ILikeBuilder
            globalIlikeBuilder = SpringUtil.getBean(ILikeBuilder.class);
        }
        return globalIlikeBuilder.ilike(column);
    }

    /**
     * 拼接and语言，仅供框架内部使用
     *
     * @param column
     * @param op
     * @param value
     * @return
     */
    public void and(String column, SqlOperation op, Object value) {
        //列名称安全
        String safeColumn = ColumnUtil.safeColumn(column);
        conditions.add(new SimpleCondition(safeColumn, op, value));
    }

    /**
     * 框架内部使用
     * 生成and ( sql1 or sql2 or sql3 ) <br>
     * 语句1: column 多个， value 一个 => and (c1 = v or c2 = v...) <br>
     * 语句2：column 一个， value 多个 => and (c like v1 or c like v2) <br>
     * 语句3：column 多个， value 多个 => and (c1 like v1 or c2 like v2 ...) 列数量和值数量必须相等<br>
     *
     * @param columns 可以有一个或多个
     * @param op
     * @param value   可以是Collection,数组或单个值
     * @return
     */
    public void andOrs(String[] columns, SqlOperation op, Object value) {
        //列名称安全
        for (int i = 0; i < columns.length; i++) {
            columns[i] = ColumnUtil.safeColumn(columns[i]);
        }
        conditions.add(new NestedOrCondition(columns, op, value));
    }

    private static <E> void appendWrapper(WhereBuilder builder, AbstractWrapper<E, String, ?> wrapper, SqlOperation op,
            String column, Object value) {
        switch (op) {
            case EQ:
                wrapper.eq(column, value);
                break;
            case NE:
                wrapper.ne(column, value);
                break;
            case LIKE:
                wrapper.like(column, value);
                break;
            case LIKE_LEFT:
                wrapper.likeLeft(column, value);
                break;
            case LIKE_RIGHT:
                wrapper.likeRight(column, value);
                break;
            case ILIKE:
                String ilikeSql = builder.buildILike(column, value);
                wrapper.apply(ilikeSql, "%" + value + "%");
                break;
            case NOT_LIKE:
                wrapper.notLike(column, value);
                break;
            case LT:
                wrapper.lt(column, value);
                break;
            case GT:
                wrapper.gt(column, value);
                break;
            case LE:
                wrapper.le(column, value);
                break;
            case GE:
                wrapper.ge(column, value);
                break;
            case BETWEEN:
                wrapper.between(column, ((Object[]) value)[0], ((Object[]) value)[1]);
                break;
            case IN:
                wrapper.in(column, (Collection<?>) value);
                break;
            case NOT_IN:
                wrapper.notIn(column, (Collection<?>) value);
                break;
            case IS_NULL:
                wrapper.isNull(column);
                break;
            case IS_NOT_NULL:
                wrapper.isNotNull(column);
                break;
            default:
                throw new IllegalArgumentException("错误的枚举：" + op);
        }
    }

    private interface Condition {
        <E> void apply(WhereBuilder builder, AbstractWrapper<E, String, ?> wrapper);
    }

    private static class SimpleCondition implements Condition {
        private final String column;
        private final SqlOperation op;
        private final Object value;

        SimpleCondition(String column, SqlOperation op, Object value) {
            this.column = column;
            this.op = op;
            this.value = value;
        }

        @Override
        public <E> void apply(WhereBuilder builder, AbstractWrapper<E, String, ?> wrapper) {
            appendWrapper(builder, wrapper, op, column, value);
        }
    }

    /**
     * and (a like %% or b like %%)
     */
    private static class NestedOrCondition implements Condition {
        private final String[] columns;
        private final SqlOperation op;
        private final Object[] values;
        private final int size;

        NestedOrCondition(String[] columns, SqlOperation op, Object values) {
            this.op = op;
            this.columns = columns;
            this.values = toArray(values);
            this.size = Math.max(columns.length, this.values.length);
        }

        private Object[] toArray(Object values) {
            if (ArrayUtil.isArray(values)) {
                return (Object[]) values;
            } else if (values instanceof Collection) {
                return ((Collection<?>) values).toArray(new Object[0]);
            }
            return new Object[]{values};
        }


        @Override
        public <E> void apply(WhereBuilder builder, AbstractWrapper<E, String, ?> wrapper) {
            Assert.isTrue(columns.length == this.values.length || columns.length == 1 || values.length == 1,
                    "参数个数错误");
            wrapper.and(wq -> {
                for (int i = 0; i < size; i++) {
                    if (i != 0) {
                        wq.or();
                    }
                    String col = columns.length == 1 ? columns[0] : columns[i];
                    Object value = values.length == 1 ? values[0] : values[i];
                    appendWrapper(builder, wq, op, col, value);
                }
            });
        }
    }

    /**
     * 框架内部使用
     *
     * @return
     */
    private <T> QueryWrapper<T> toQueryWrapper() {
        if (queryWrapper == null) {
            queryWrapper = Wrappers.query();
            apply(queryWrapper);
        }
        return queryWrapper;
    }

    /**
     * 查询条件应用到wrapper上，仅供框架内部使用
     *
     * @param wrapper
     */
    public void apply(AbstractWrapper<?, String, ?> wrapper) {
        for (Condition condition : conditions) {
            condition.apply(this, wrapper);
        }
    }

    protected <A> A trimToNull(A value) {
        if (value == null) {
            return null;
        }
        if (value instanceof CharSequence) {
            return (A) StrUtil.trimToNull((CharSequence) value);
        }
        return value;
    }

    /**
     * 框架内部使用
     *
     * @return
     */
    public Map<String, Object> getParamNameValuePairs() {
        return toQueryWrapper().getParamNameValuePairs();
    }

    /**
     * 用于在XML中使用where条件，例如:
     * <code><pre>
     *  Mapper接口：
     *  List selectByWhere(Where&lt;T&gt; where);
     *  XML:
     * `select xxx from table ${where}`
     * </pre></code>
     *
     * @return
     */
    @Override
    public String toString() {
        String customSqlSegment = toQueryWrapper().getCustomSqlSegment();
        //把sql中的ew.替换成where.
        return customSqlSegment.replaceAll("ew\\.", "where.");
    }

    /**
     * 用与一个大sql中有多个where对象的情况
     *
     * @param paramName Mapper接口上定义的参数名字
     * @return
     */
    public String toString(Object paramName) {
        String customSqlSegment = toQueryWrapper().getCustomSqlSegment();
        //把sql中的ew.替换成where.
        return customSqlSegment.replaceAll("ew\\.", paramName + ".");
    }
}
