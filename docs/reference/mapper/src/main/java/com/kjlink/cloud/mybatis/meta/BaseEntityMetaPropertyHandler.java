package com.kjlink.cloud.mybatis.meta;

import java.time.LocalDateTime;

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kjlink.cloud.mybatis.entity.BaseEntity;


/**
 * 自动注入BaseEntity中的属性字段
 *
 * @author Fulai
 * @since 2023-04-10
 */
public class BaseEntityMetaPropertyHandler implements MetaPropertyHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEntityMetaPropertyHandler.class);

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
            if (StrUtil.isBlank(be.getCreateBy())) {
                be.setCreateBy(username);
            }
            if (be.getCreateTime() == null) {
                be.setCreateTime(LocalDateTime.now());
            }
            if (StrUtil.isBlank(be.getUpdateBy())) {
                be.setUpdateBy(username);
            }
            if (be.getUpdateTime() == null) {
                be.setUpdateTime(LocalDateTime.now());
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
            be.setUpdateBy(username);
            be.setUpdateTime(LocalDateTime.now());
        }
    }
}
