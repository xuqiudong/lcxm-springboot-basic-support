package cn.xuqiudong.common.base.fill.impl;

import cn.xuqiudong.common.base.entity.BaseMpEntity;
import cn.xuqiudong.common.base.fill.AutoFillFieldHandler;
import cn.xuqiudong.common.helper.CurrentUserInfoHelper;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * 描述:
 * BaseMpEntity 类型的实体属性填充处理
 *
 * @author Vic.xu
 * @since 2025-09-10 14:24
 */
public class BaseMpEntityAutoFillFieldHandler implements AutoFillFieldHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMpEntityAutoFillFieldHandler.class);

    public static final String TIPS = "未提供获取当前登录用户的方法, 将使用匿名用户{}";

    @Override
    public void insertFill(MetaObject metaObject) {
        Object entity = metaObject.getOriginalObject();
        if (entity instanceof BaseMpEntity baseMpEntity) {
            tips("insert");

            String useId = CurrentUserInfoHelper.getUserId();
            // 字段为空的时候 设置相关默认值

            // 创建人
            if (baseMpEntity.getCreateBy() == null) {
                baseMpEntity.setCreateBy(useId);
            }
            // 创建时间
            if (baseMpEntity.getCreateTime() == null) {
                baseMpEntity.setCreateTime(LocalDateTime.now());
            }

            // 修改人
            if (baseMpEntity.getUpdateBy() == null) {
                baseMpEntity.setUpdateBy(useId);
            }
            // 修改时间
            if (baseMpEntity.getUpdateTime() == null) {
                baseMpEntity.setUpdateTime(LocalDateTime.now());
            }

        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object entity = metaObject.getOriginalObject();
        if (entity instanceof BaseMpEntity baseMpEntity) {
            tips("update");
            String useId = CurrentUserInfoHelper.getUserId();
            // 总是更新 修改人和修改时间
            baseMpEntity.setUpdateBy(useId);
            baseMpEntity.setUpdateTime(LocalDateTime.now());
        }

    }

    private void tips(String opt) {
        if (CurrentUserInfoHelper.isAnonymous()) {
            LOGGER.warn(TIPS,  opt);
        }
    }
}
