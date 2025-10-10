package com.kjlink.cloud.mybatis;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kjlink.cloud.mybatis.entity.Role;
import com.kjlink.cloud.mybatis.entity.User;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2024-01-19
 */
@SpringBootTest(classes = MyBatisTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TableScanTest {
    @Autowired
    private BasicMapper<User> userBasicMapper;
    @Autowired
    private BasicMapper<Role> roleBasicMapper;

    @Test
    void test() {
        Assertions.assertThat(userBasicMapper).isNotNull();
        Assertions.assertThat(roleBasicMapper).isNotNull();
        List<Role> list = roleBasicMapper.selectList(null);
        Assertions.assertThat(list).isNotNull();
    }
}
