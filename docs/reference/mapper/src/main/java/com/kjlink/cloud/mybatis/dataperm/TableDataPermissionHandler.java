package com.kjlink.cloud.mybatis.dataperm;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单表数据权限处理器，适配成多表
 *
 * @author Fulai
 * @since 2025-07-21
 */
public class TableDataPermissionHandler implements MultiDataPermissionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TableDataPermissionHandler.class);
    private final String matchTable;
    private final String sql;
    private final DataPermissionSqlResolver resolver;
    private String firstTable;

    public TableDataPermissionHandler(String matchTable, String sql, DataPermissionSqlResolver resolver) {
        this.matchTable = matchTable;
        this.sql = sql;
        this.resolver = resolver;
    }

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        if (isTableMatch(table)) {
            LOG.debug("为表{}注入数据权限sql", table.getName());
            try {
                String alias = table.getAlias() == null ? null : table.getAlias().getName();
                String resolveSql = resolver.resolve(table.getName(), alias, sql);
                if (resolveSql == null || resolveSql.isEmpty()) {
                    return null;
                }
                return CCJSqlParserUtil.parseCondExpression(resolveSql);
            } catch (Exception e) {
                throw new MybatisPlusException(e);
            }
        }
        return null;
    }

    private boolean isTableMatch(Table table) {
        if (StrUtil.isNotEmpty(matchTable)) {
            return matchTable.equalsIgnoreCase(table.getName());
        }
        //未指定主表时，取第一个
        if (firstTable == null) {
            firstTable = table.getName();
            return true;
        }
        return firstTable.equalsIgnoreCase(table.getName());
    }
}
