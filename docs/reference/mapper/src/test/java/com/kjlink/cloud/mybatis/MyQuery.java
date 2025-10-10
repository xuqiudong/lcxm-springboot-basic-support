package com.kjlink.cloud.mybatis;

import com.kjlink.cloud.mybatis.query.SqlCondition;
import com.kjlink.cloud.mybatis.query.SqlOperation;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2024-01-15
 */
public class MyQuery {
    @SqlCondition(op = SqlOperation.LIKE)
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
