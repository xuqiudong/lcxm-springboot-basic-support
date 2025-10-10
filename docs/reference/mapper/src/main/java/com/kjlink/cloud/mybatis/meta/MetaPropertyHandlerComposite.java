package com.kjlink.cloud.mybatis.meta;

import java.util.List;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 由于MBP官方仅支持一个MetaObjectHandler，所以这里采用了组合设计模式。
 * 组合多个MetaPropertyHandler实现，方便各微服务自行扩展
 *
 * @author Fulai
 * @see MetaPropertyHandler
 * @since 2023-04-10
 */
public class MetaPropertyHandlerComposite implements MetaObjectHandler {
    private final List<MetaPropertyHandler> propertyHandlers;

    public MetaPropertyHandlerComposite(List<MetaPropertyHandler> customizers) {
        this.propertyHandlers = customizers;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        for (MetaPropertyHandler propertyHandler : propertyHandlers) {
            propertyHandler.insertFill(metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        for (MetaPropertyHandler propertyHandler : propertyHandlers) {
            propertyHandler.updateFill(metaObject);
        }
    }
}
