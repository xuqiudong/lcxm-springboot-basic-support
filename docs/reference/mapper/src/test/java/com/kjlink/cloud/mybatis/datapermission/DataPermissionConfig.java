package com.kjlink.cloud.mybatis.datapermission;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.kjlink.cloud.mybatis.dataperm.SimpleDataPermissionSqlResolver;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-07-21
 */
@TestConfiguration
public class DataPermissionConfig {
    @Bean
    public SimpleDataPermissionSqlResolver simpleDataPermissionSqlResolver() {
        return new SimpleDataPermissionSqlResolver();
    }
}
