/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kjlink.cloud.mybatis.impl;

import java.lang.reflect.Method;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.javassist.util.proxy.MethodHandler;

class ModifyAttrsRecordHandler<T> implements MethodHandler, UpdateWrapperBuilder<T> {
    private final UpdateWrapper<T> updateWrapper;
    private final Class<T> entityClass;
    private final TableInfo tableInfo;

    ModifyAttrsRecordHandler(Class<T> entityClass, TableInfo tableInfo) {
        this.entityClass = entityClass;
        this.tableInfo = tableInfo;
        this.updateWrapper = Wrappers.update();
    }

    @Override
    public Object invoke(Object self, Method originalMethod, Method proxyMethod, Object[] args) throws Throwable {

        String methodName = originalMethod.getName();
        if (methodName.startsWith("set")
                && methodName.length() > 3
                && Character.isUpperCase(methodName.charAt(3))
                && originalMethod.getParameterCount() == 1) {

            String fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

            //是主键
            if (StrUtil.equals(fieldName, tableInfo.getKeyProperty())) {
                //where id = ?
                updateWrapper.eq(tableInfo.getKeyColumn(), args[0]);
                return null;
            }

            String columnName = fieldName;
            // 利用 TableInfo 查找 columnName
            TableFieldInfo fieldInfo = tableInfo.getFieldList()
                    .stream()
                    .filter(f -> f.getProperty().equals(fieldName))
                    .findFirst()
                    .orElse(null);
            if (fieldInfo != null) {
                columnName = fieldInfo.getColumn();
            }
            updateWrapper.set(columnName, args[0]);
            return null;
        }

        // 接口方法
        if (originalMethod.getDeclaringClass() == UpdateWrapperBuilder.class) {
            if ("getWrapper".equals(methodName)) {
                return getWrapper();
            }
            if ("getEntityClass".equals(methodName)) {
                return getEntityClass();
            }
        }

        return proxyMethod.invoke(self, args);
    }

    @Override
    public UpdateWrapper<T> getWrapper() {
        return updateWrapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }
}
