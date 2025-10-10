package com.kjlink.cloud.mybatis.dataperm;

import java.sql.SQLException;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 单表数据权限拦截器
 *
 * @author Fulai
 * @since 2025-07-18
 */
public class TableDataPermissionInterceptor implements InnerInterceptor {
    private DataPermissionSqlResolver resolver;

    public TableDataPermissionInterceptor(DataPermissionSqlResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
            ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        String id = ms.getId();
        DataPermissionUtil.DataPermissionConfig config = DataPermissionUtil.getAndRemove(id);
        if (config != null) {
            DataPermissionHandler handler =
                    new TableDataPermissionHandler(config.table(), config.sql(), resolver);
            //调用官方的数据权限拦截器
            DataPermissionInterceptor delegation = new DataPermissionInterceptor(handler);
            delegation.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
        }
    }
}
