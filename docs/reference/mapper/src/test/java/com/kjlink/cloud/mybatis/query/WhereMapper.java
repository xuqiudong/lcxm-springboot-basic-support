package com.kjlink.cloud.mybatis.query;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.kjlink.cloud.mybatis.entity.User;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2024-01-22
 */
@Mapper
public interface WhereMapper {
    List<User> selectByWhere(@Param("where") Where where, OrderBy orderBy);

    @Select("""
            select * from (
            select * from user ${where1.toString('where1')}
            ) t ${where2.toString('where2')}
            """)
    List<User> selectByWhere2(Where where1, Where where2);
}
