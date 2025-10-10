package com.kjlink.cloud.mybatis.legacy;

import com.kjlink.cloud.mybatis.GenericCrudMapper;

/**
 * ID为Long类型的单表增删改查Mapper，通常用于Mysql数据库
 * 兼容历史项目，不推荐继续使用，请尽快升级到String型id，方便适配各种国产数据库
 *
 * @author Fulai
 * @since 2025-07-01
 */
@Deprecated
public interface ExtendMapper<T extends BaseEntity> extends GenericCrudMapper<Long, T> {
}
