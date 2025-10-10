package com.kjlink.cloud.mybatis;

import org.apache.ibatis.annotations.Mapper;

import com.kjlink.cloud.mybatis.entity.User;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-07-01
 */
@Mapper
public interface UserMapper extends CrudMapper<User> {
}
