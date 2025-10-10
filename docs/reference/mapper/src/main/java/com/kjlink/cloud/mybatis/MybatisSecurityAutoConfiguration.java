package com.kjlink.cloud.mybatis;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import com.kjlink.cloud.mybatis.meta.CurrentUserInfoUtil;
import com.kjlink.cloud.security.SecurityUtil;

/**
 * 配置Mybatis-Mapper模板获取当前用户
 *
 * @author Fulai
 * @since 2025-05-06
 */
@AutoConfiguration
@ConditionalOnClass(SecurityUtil.class)
public class MybatisSecurityAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        CurrentUserInfoUtil.setIsAnonymousSupplier(SecurityUtil::isAnonymous);
        CurrentUserInfoUtil.setUsernameSupplier(SecurityUtil::getUsername);
        CurrentUserInfoUtil.setOrganizationIdSupplier(SecurityUtil::getOrgId);
    }
}
