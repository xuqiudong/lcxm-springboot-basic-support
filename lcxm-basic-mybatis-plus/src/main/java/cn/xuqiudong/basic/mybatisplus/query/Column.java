package cn.xuqiudong.basic.mybatisplus.query;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * 描述:
 *  对应实体的GETTER 方法
 *  形如：XxxEntity::getName
 * @author Vic.xu
 * @since 2025-10-29 14:16
 */
@FunctionalInterface
public interface Column<T, R> extends SFunction<T, R> {
}
