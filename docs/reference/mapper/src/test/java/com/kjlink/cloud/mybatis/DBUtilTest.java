package com.kjlink.cloud.mybatis;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.kjlink.cloud.mybatis.entity.User;
import com.kjlink.cloud.mybatis.impl.UpdateWrapperBuilder;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-08-28
 */
@SpringBootTest
class DBUtilTest {

    @Test
    void updateWrap() {
        User user = DBUtil.updateWrap(User.class);
        user.setId("123");
        user.setEmail("456");
        user.setFullName(null);

        Class<? extends User> aClass = user.getClass();

        UpdateWrapper wrapper = ((UpdateWrapperBuilder) user).getWrapper();
        String customSqlSegment = wrapper.getCustomSqlSegment();
        System.out.println(customSqlSegment);
    }
}