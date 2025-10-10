package com.kjlink.cloud.mybatis.injector;

/**
 * 忽略全局配置，更新全部字段
 *
 * @author kj
 * @since 2023-01-31
 */
public class UpdateAll extends UpdateByIdMethod {
    public static final String METHOD_NAME = "updateAll";

    public UpdateAll() {
        super(METHOD_NAME, UpdateType.FULL);
    }
}
