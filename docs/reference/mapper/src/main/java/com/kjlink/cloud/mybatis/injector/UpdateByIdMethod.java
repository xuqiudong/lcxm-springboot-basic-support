package com.kjlink.cloud.mybatis.injector;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import static java.util.stream.Collectors.joining;

/**
 * 扩展的updateById方法，参考{@linkplain com.baomidou.mybatisplus.core.injector.methods.UpdateById}
 *
 * @author kj
 * @since 2023-02-07
 */
class UpdateByIdMethod extends AbstractMethod {
    private static final String SQL = "<script>%nUPDATE %s %s WHERE %s=#{%s} %s%n</script>";
    //更新类型
    private final UpdateType updateType;

    protected enum UpdateType {
        FULL, PARTIAL
    }

    UpdateByIdMethod(String methodName, UpdateType updateType) {
        super(methodName);
        this.updateType = updateType;
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        final String additional = optlockVersion(tableInfo) + tableInfo.getLogicDeleteSql(true, true);
        String setSql = buildSetSql(tableInfo, ENTITY_DOT);

        String sql = String.format(SQL, tableInfo.getTableName(), setSql, tableInfo.getKeyColumn(),
                ENTITY_DOT + tableInfo.getKeyProperty(), additional);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, methodName, sqlSource);
    }


    /**
     * SQL 更新 set 语句
     *
     * @param table  表信息
     * @param prefix 前缀
     * @return sql
     */
    protected String buildSetSql(TableInfo table, final String prefix) {
        String sqlScript = this.getAllSqlSet(table, prefix);
        sqlScript = SqlScriptUtils.convertSet(sqlScript);
        return sqlScript;
    }

    /**
     * 获取所有的 sql set 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getAllSqlSet(TableInfo table, final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;

        List<TableFieldInfo> fieldList = table.getFieldList();
        //是否过滤掉逻辑删除字段
        boolean ignoreLogicDelFiled = table.isWithLogicDelete();
        boolean withLogicDelete = table.isWithLogicDelete();
        return fieldList.stream().filter(i -> {
            if (ignoreLogicDelFiled) {
                return !(withLogicDelete && i.isLogicDelete());
            }
            return true;
        }).map(i -> getSqlSet(i, newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    /**
     * 获取 set sql 片段
     *
     * @param prefix 前缀
     * @return sql 脚本片段
     */
    public String getSqlSet(TableFieldInfo fieldInfo, final String prefix) {
        //创建人、创建时间从不更新
        if (fieldInfo.getUpdateStrategy() == FieldStrategy.NEVER) {
            return null;
        }

        final String newPrefix = prefix == null ? EMPTY : prefix;
        // 默认: column=
        String column = fieldInfo.getColumn();
        String update = fieldInfo.getUpdate();

        String sqlSet = column + EQUALS;
        if (StringUtils.isNotBlank(update)) {
            sqlSet += String.format(update, column);
        } else {
            String el = fieldInfo.getEl();
            sqlSet += SqlScriptUtils.safeParam(newPrefix + el);
        }
        sqlSet += COMMA;

        //update时能自动填充的，总是update
        boolean withUpdateFill = fieldInfo.isWithUpdateFill();
        if (withUpdateFill) {
            // 不进行 if 包裹
            return sqlSet;
        }

        // 总是update
        if (updateType == UpdateType.FULL) {
            return sqlSet;
        }

        //选择性update
        String property = fieldInfo.getProperty();
        return convertIf(fieldInfo, sqlSet, convertIfProperty(newPrefix, property));
    }

    private String convertIfProperty(String prefix, String property) {
        return StringUtils.isNotBlank(prefix) ? prefix.substring(0, prefix.length() - 1) + "['" + property + "']" :
                property;
    }

    /**
     * 转换成 if 标签的脚本片段
     *
     * @param sqlScript sql 脚本片段
     * @param property  字段名
     * @return if 脚本片段
     */
    private String convertIf(TableFieldInfo fieldInfo, final String sqlScript, final String property) {
        //基本类型总是更新
        boolean isPrimitive = fieldInfo.isPrimitive();
        if (isPrimitive) {
            return sqlScript;
        }
        //其他非null
        return SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", property), false);
    }
}
