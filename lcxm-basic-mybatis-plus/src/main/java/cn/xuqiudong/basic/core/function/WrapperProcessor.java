package cn.xuqiudong.basic.core.function;

import cn.xuqiudong.basic.core.enums.QueryOperation;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;

/**
 * 描述:
 *   处理查询条件的函数式接口
 * @see QueryOperation
 * @author Vic.xu
 * @since 2025-10-31 15:56
 */
@FunctionalInterface
public interface WrapperProcessor<E> {

    void process(AbstractWrapper<E, String, ?> wrapper, String column, Object value);
}
