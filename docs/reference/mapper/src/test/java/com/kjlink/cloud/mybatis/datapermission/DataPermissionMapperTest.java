package com.kjlink.cloud.mybatis.datapermission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.kjlink.cloud.mybatis.entity.User;
import com.kjlink.cloud.mybatis.query.PageQuery;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-07-08
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(DataPermissionConfig.class)
class DataPermissionMapperTest {
    @Autowired
    private DataPermissionMapper dataPermissionMapper;

    @Test
    void filterUser() {
        Page<User> page = new Page(0, 1);
        dataPermissionMapper.filterUser(page);
    }

    @Test
    void test2() {
        dataPermissionMapper.filterUser2("admin");
    }

    @Test
    void test3() {
        dataPermissionMapper.withPermission("t.update_by = {{USERNAME}}")
                .selectPage(new PageQuery());
    }
}