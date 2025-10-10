package cn.xuqiudong.common.base.fill.impl;

import cn.xuqiudong.common.base.entity.BaseMpEntity;
import cn.xuqiudong.common.base.fill.AutoFillFieldHandler;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 描述:
 * BaseMpEntity 类型的实体属性填充处理
 *
 * @author Vic.xu
 * @since 2025-09-10 14:24
 */
public class BaseMpEntityAutoFillFieldHandler implements AutoFillFieldHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Object entity = metaObject.getOriginalObject();
        if (entity instanceof BaseMpEntity baseMpEntity) {
            //TODO
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object entity = metaObject.getOriginalObject();
        if (entity instanceof BaseMpEntity baseMpEntity) {
            //TODO
        }

    }
}
