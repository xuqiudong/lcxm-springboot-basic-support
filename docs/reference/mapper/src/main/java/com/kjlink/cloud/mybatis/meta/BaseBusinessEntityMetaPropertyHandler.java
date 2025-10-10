package com.kjlink.cloud.mybatis.meta;

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kjlink.cloud.mybatis.entity.BaseBusinessEntity;

/**
 * 注入BaseBusinessEntity属性
 *
 * @author Fulai
 * @since 2023-04-11
 */
public class BaseBusinessEntityMetaPropertyHandler implements MetaPropertyHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEntityMetaPropertyHandler.class);
    private static final String TIPS = "找不到当前登录用户，将使用匿名用户";

    @Override
    public void insertFill(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof BaseBusinessEntity bbe) {
            if (CurrentUserInfoUtil.isAnonymous()) {
                LOG.warn(TIPS);
            }

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
        //noop
    }
}
