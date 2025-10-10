package com.kjlink.cloud.mybatis.datapermission;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kjlink.cloud.mybatis.CrudMapper;
import com.kjlink.cloud.mybatis.annotation.DataPermission;
import com.kjlink.cloud.mybatis.entity.User;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-07-08
 */
@Mapper
public interface DataPermissionMapper extends CrudMapper<User> {
    @Select("""
            select * from t_sys_user x 
            """)
    @DataPermission("t.id in (1,2)")
    Page<User> filterUser(Page<User> page);

    @Select("""
            select * from t_sys_user x1
            LEFT JOIN t_sys_role b on b.id = x1.id
            where x1.user_name = #{userName}
            """)
    @DataPermission(table = "t_sys_role", value = "t.update_time < now()")
    List<User> filterUser2(String userName);
}
