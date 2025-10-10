package com.kjlink.cloud.mybatis.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

public interface UpdateWrapperBuilder<T> {
    UpdateWrapper<T> getWrapper();
    Class<T> getEntityClass();
}