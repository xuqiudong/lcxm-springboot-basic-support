package com.kjlink.cloud.mybatis.injector;

/**
 * 忽略全局配置，更新非空字段
 *
 * @author kj
 * @since 2023-01-31
 */
public class UpdatePartial extends UpdateByIdMethod {
    public static final String METHOD_NAME = "updatePartial";

    public UpdatePartial() {
        super(METHOD_NAME, UpdateType.PARTIAL);
    }
}
