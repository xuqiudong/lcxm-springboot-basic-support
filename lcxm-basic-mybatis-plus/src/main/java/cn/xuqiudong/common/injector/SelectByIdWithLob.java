package cn.xuqiudong.common.injector;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 描述:
 *    查询全部， 包括 select = false 字段  TODO  test
 * @see com.baomidou.mybatisplus.core.injector.methods.SelectById
 * @author Vic.xu
 * @since 2025-10-25 15:59
 */
public class SelectByIdWithLob extends AbstractMethod {

    private static final String METHOD_NAME = "selectByIdWithLob";

    /**
     * @since 3.5.0
     */
    protected SelectByIdWithLob() {
        super(METHOD_NAME);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {

        SqlMethod sqlMethod = SqlMethod.SELECT_BY_ID;
        String sql = String.format(sqlMethod.getSql(),
                sqlSelectColumns(tableInfo, false),
                tableInfo.getTableName(),
                tableInfo.getKeyColumn(),
                tableInfo.getKeyProperty(),
                tableInfo.getLogicDeleteSql(true, true));
        SqlSource sqlSource = super.createSqlSource(configuration, sql, Object.class);

        return this.addSelectMappedStatementForTable(mapperClass, methodName, sqlSource, tableInfo);
    }

    /**
     * SQL 查询所有表字段
     *
     * @param table        表信息
     * @param queryWrapper 是否为使用 queryWrapper 查询
     * @return sql 脚本
     */
    protected String sqlSelectColumns(TableInfo table, boolean queryWrapper) {
        /* 假设存在用户自定义的 resultMap 映射返回 */
        String selectColumns = ASTERISK;
        if (table.getResultMap() == null || table.isAutoInitResultMap()) {
            /* 未设置 resultMap 或者 resultMap 是自动构建的,视为属于mp的规则范围内 */
            selectColumns = table.getAllSqlSelect();
        }
        return selectColumns;
    }
}
