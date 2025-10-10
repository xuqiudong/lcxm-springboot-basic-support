package com.kjlink.cloud.mybatis.injector;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-08-29
 */
public class SelectByIdWithLob extends AbstractMethod {

    public SelectByIdWithLob() {
        super("selectByIdWithLob");
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql = String.format(SqlMethod.SELECT_BY_ID.getSql(),
                sqlSelectAllColumns(tableInfo),
                tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
                tableInfo.getLogicDeleteSql(true, true));

        SqlSource sqlSource = createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, methodName, sqlSource, tableInfo);
    }

    protected String sqlSelectAllColumns(TableInfo table) {
        /* 假设存在用户自定义的 resultMap 映射返回 */
        String selectColumns = ASTERISK;
        if (table.getResultMap() == null || table.isAutoInitResultMap()) {
            /* 未设置 resultMap 或者 resultMap 是自动构建的,视为属于mp的规则范围内 */
            selectColumns = table.chooseSelect(x -> true);
        }
        return selectColumns;
    }
}
