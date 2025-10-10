package com.kjlink.cloud.mybatis.legacy;

import java.time.LocalDateTime;

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kjlink.cloud.mybatis.meta.CurrentUserInfoUtil;
import com.kjlink.cloud.mybatis.meta.MetaPropertyHandler;

/**
 * 注入BaseBusinessEntity属性
 *
 * @author Fulai
 * @since 2023-04-11
 */
public class LegacyEntityMetaPropertyHandler implements MetaPropertyHandler {
    private static final Logger LOG = LoggerFactory.getLogger(LegacyEntityMetaPropertyHandler.class);
    private static final String TIPS = "找不到当前登录用户，将使用匿名用户";

    @Override
    public void insertFill(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();

        if (originalObject instanceof BaseEntity be) {
            if (CurrentUserInfoUtil.isAnonymous()) {
                LOG.warn(TIPS);
            }
            String username = CurrentUserInfoUtil.getUsername();

            //默认填充创建人、创建时间、修改人、修改时间
            if (StrUtil.isBlank(be.getCreatedBy())) {
                be.setCreatedBy(username);
            }
            if (be.getCreatedTime() == null) {
                be.setCreatedTime(LocalDateTime.now());
            }
            if (StrUtil.isBlank(be.getUpdatedBy())) {
                be.setUpdatedBy(username);
            }
            if (be.getUpdatedTime() == null) {
                be.setUpdatedTime(LocalDateTime.now());
            }
        }

        if (originalObject instanceof BaseBusinessEntity bbe) {
            //填充负责人
            if (StrUtil.isBlank(bbe.getOwnerBy())) {
                bbe.setOwnerBy(CurrentUserInfoUtil.getUsername());
            }

            //填充部门id
            if (bbe.getOrganizationId() == null) {
                bbe.setOrganizationId(CurrentUserInfoUtil.getOrganizationId());
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof BaseEntity be) {
            if (CurrentUserInfoUtil.isAnonymous()) {
                LOG.warn(TIPS);
            }
            String username = CurrentUserInfoUtil.getUsername();

            //总是更新修改人和修改时间
            be.setUpdatedBy(username);
            be.setUpdatedTime(LocalDateTime.now());
        }
    }
}
