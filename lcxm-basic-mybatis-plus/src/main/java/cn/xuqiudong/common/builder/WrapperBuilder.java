package cn.xuqiudong.common.builder;

import cn.hutool.core.util.ArrayUtil;
import cn.xuqiudong.common.enums.QueryOperation;
import cn.xuqiudong.common.query.Where;
import cn.xuqiudong.common.util.ColumnUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 描述:
 * 查询对象构建器:   QueryWrapper
 *
 * @author Vic.xu
 * @see cn.xuqiudong.common.util.QueryConditionUtils
 * @since 2025-10-29 17:12
 */
public class WrapperBuilder {

    /**
     * 中间需要添加的条件
     */
    private List<Condition> conditions;

    /**
     * 最终构件的查询对象, 在toWrapper()方法中构建一次
     */
    private QueryWrapper queryWrapper;

    private Where where;


    private WrapperBuilder() {
        this.conditions = new ArrayList<>();

    }

    public static WrapperBuilder create() {
        return new WrapperBuilder();
    }

    /**
     * 构建查询对象
     */
    public <T> QueryWrapper<T> toWrapper() {
        if (this.queryWrapper == null) {
            queryWrapper = Wrappers.query();
            // 拼接条件
            for (Condition condition : conditions) {
                condition.build(queryWrapper);
            }
        }
        return queryWrapper;
    }

    public <T> Where<T> toWhere() {
        if (this.where == null) {
            where = new Where();
            // 拼接条件
            for (Condition condition : conditions) {
                condition.build(where);
            }
        }
        return where;
    }


    /**
     * 添加条件
     */
    public WrapperBuilder addCondition(String column, QueryOperation operation, Object value) {
        column = ColumnUtils.safeColumn(column);
        this.conditions.add(new NormalCondition(column, operation, value));
        return this;
    }

    /**
     * 添加嵌套条件 内部多个条件 之间是or关系
     */
    public void addNestedOrCondition(String[] columns, QueryOperation operation, Object value) {
        for (int i = 0; i < columns.length; i++) {
            columns[i] = ColumnUtils.safeColumn(columns[i]);
        }
        this.conditions.add(new NestedOrCondition(columns, operation, value));
    }


    private interface Condition {
        <T> void build(QueryWrapper<T> queryWrapper);
    }


    /**
     * 普通条件
     */
    private static class NormalCondition implements Condition {
        String column;
        QueryOperation operation;
        Object value;

        public NormalCondition(String column, QueryOperation operation, Object value) {
            this.column = column;
            this.operation = operation;
            this.value = value;
        }

        @Override
        public <T> void build(QueryWrapper<T> queryWrapper) {
            operation.processWrapper(queryWrapper, column, value);
        }
    }

    /**
     * 多条件嵌套or
     */
    private static class NestedOrCondition implements Condition {

        String[] columns;
        QueryOperation operation;
        Object[] values;
        int size;

        public NestedOrCondition(String[] columns, QueryOperation operation, Object value) {
            this.columns = columns;
            this.operation = operation;
            this.values = toArray(value);
            this.size = Math.max(columns.length, values.length);
        }

        private Object[] toArray(Object value) {
            if (ArrayUtil.isArray(value)) {
                return (Object[]) value;
            } else if (value instanceof Collection) {
                return ((Collection) value).toArray(new Object[0]);
            }
            return new Object[]{value};
        }

        @Override
        public <T> void build(QueryWrapper<T> queryWrapper) {
            queryWrapper.and(w -> {
                for (int i = 0; i < size; i++) {
                    if (i != 0) {
                        w.or();
                    }
                    String col = columns.length == 1 ? columns[0] : columns[i];
                    Object value = values.length == 1 ? values[0] : values[i];
                    operation.processWrapper(w, col, value);
                }
            });
        }
    }
}
