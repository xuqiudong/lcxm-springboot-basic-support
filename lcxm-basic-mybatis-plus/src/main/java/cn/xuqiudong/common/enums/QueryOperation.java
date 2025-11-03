package cn.xuqiudong.common.enums;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.xuqiudong.common.function.WrapperProcessor;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;

import java.util.Collection;
import java.util.Collections;

/**
 * 描述:
 * 查询操作类型枚举（对应SQL中的条件运算符）
 * <p>
 * 1. 暂时移除 BETWEEN 和 NOT BETWEEN 支持， 可以通过 GE 和 LE 等效实现
 * </p>
 *
 * @author Vic.xu
 * @since 2025-10-29 15:30
 */
public enum QueryOperation {

    EQ(false, (wrapper, column, value) -> wrapper.eq(column, value)),

    LIKE(false, (wrapper, column, value) -> wrapper.like(column, value)),

    IN(true, (wrapper, column, value) -> wrapper.in(column, toCollection(value))),

    NOT_IN(true, (wrapper, column, value) -> wrapper.notIn(column, toCollection(value))),

    IS_NULL(false, (wrapper, column, value) -> wrapper.isNull(column)),

    IS_NOT_NULL(false, (wrapper, column, value) -> wrapper.isNotNull(column)),

    LT(false, (wrapper, column, value) -> wrapper.lt(column, value)),

    LE(false, (wrapper, column, value) -> wrapper.le(column, value)),

    GT(false, (wrapper, column, value) -> wrapper.gt(column, value)),

    GE(false, (wrapper, column, value) -> wrapper.ge(column, value));


    /**
     * 是否是多值: 需要将字符串分割为数组
     */
    private final boolean multiValue;

    /**
     * 处理查询条件的函数（替代抽象方法）
     */
    private final WrapperProcessor<?> processor;


    // 构造器：传入多值标识和处理函数
    QueryOperation(boolean multiValue, WrapperProcessor<?> processor) {
        this.multiValue = multiValue;
        this.processor = processor;
    }


    /**
     * 对外提供的处理方法，调用函数式接口的实现
     */
    @SuppressWarnings("unchecked")
    public <E> void processWrapper(AbstractWrapper<E, String, ?> wrapper, String column, Object value) {
        ((WrapperProcessor<E>) processor).process(wrapper, column, value);
    }


    public boolean isMultiValue() {
        return multiValue;
    }


    /**
     * 转换为集合 其实在创建WrapperBuilder的时候已经处理了:
     * value = StrUtil.split((String) value, queryField.getDelimiter());
     *
     * @see cn.xuqiudong.common.util.QueryConditionUtils#builder(Object)
     */
    private static Collection<?> toCollection(Object value) {
        if (value instanceof Collection<?>) {
            return (Collection<?>) value;
        }
        if (ArrayUtil.isArray(value)) {
            return Convert.toList(value);
        }
        return Collections.singletonList(value);
    }
}
