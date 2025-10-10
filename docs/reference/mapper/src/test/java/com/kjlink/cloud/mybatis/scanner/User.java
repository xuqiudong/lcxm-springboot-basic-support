package com.kjlink.cloud.mybatis.scanner;

import com.baomidou.mybatisplus.annotation.TableName;

import com.kjlink.cloud.mybatis.entity.BaseEntity;

/**
 * 测试重名实体类
 *
 * @author Fulai
 * @since 2024-02-04
 */
@TableName("duplicate_user_table")
public class User extends BaseEntity {

}
