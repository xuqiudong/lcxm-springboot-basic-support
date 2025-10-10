package com.kjlink.cloud.mybatis;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kjlink.cloud.mybatis.entity.Role;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2024-01-16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class VersionTest {
    @Autowired
    private RoleMapper mmf;

    @Test
    public void testUpdateTimeAsVersion() {
        Role role = new Role();
        role.setRoleCode("test");
        role.setCreateTime(LocalDateTime.now());
        role.setCreateBy("test");
        role.setUpdateTime(LocalDateTime.now());
        role.setUpdateBy("test");
        //user.setDataVersion(1);
        mmf.insert(role);
        mmf.updatePartial(role);
        SqlLog.assertContains("WHERE ID=? AND UPDATE_TIME=?");
        mmf.updateColumnById(role.getId(), Role::setRoleName, "角色");
        SqlLog.assertNotContains("UPDATE_TIME");
    }
}
