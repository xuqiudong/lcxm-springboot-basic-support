package com.kjlink.cloud.mybatis;

import org.jspecify.annotations.NonNull;

/**
 * 将前端传来的明细表和数据库已有的表进行合并
 * T 数据库类型
 * R 要保存的临时对象类型
 *
 * @author Fulai
 * @since 2025-09-01
 */
public interface MergeHandler<T, R> {
    /**
     * 1.循环数据库记录，找到和要保存的记录相同的行
     *
     * @param entity 数据库记录
     * @param record 要保存的记录
     * @return
     */
    boolean match(@NonNull T entity, @NonNull R record);

    /**
     * 2.如果相同，则更新数据库记录
     *
     * @param entity 数据库记录
     * @return true表示需要更新，false表示不需要update
     */
    boolean update(@NonNull T entity, @NonNull R record);

    /**
     * 3.要保存的记录不存在于数据库中，则创建一条新记录并插入。
     * 如果返回null将被忽略
     *
     * @param record 要保存的记录
     * @return 数据库记录对象
     */
    T create(@NonNull R record);

}
