package cn.xuqiudong.generator.dao;

import cn.xuqiudong.common.util.sql.SqlUtil;
import cn.xuqiudong.generator.contant.DatabaseType;
import cn.xuqiudong.generator.model.ColumnEntity;
import cn.xuqiudong.generator.model.TableEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2024-07-24 11:31
 */
public class GaussGeneratorDao extends BaseGeneratorDao {

    public GaussGeneratorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    String from = " FROM pg_class a" +
            " JOIN pg_namespace b ON a.relnamespace = b.oid " +
            " LEFT JOIN pg_description c ON a.oid = c.objoid AND c.objsubid = '0' " +
            " LEFT JOIN pg_tables d on d.tablename = a.relname " +
            " WHERE a.relkind = 'r' and b.nspname =  current_schema ";

    Set<String> sortableColumns = Stream.of("tableName", "comments", "createTime").collect(Collectors.toSet());

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.gauss;
    }

    @Override
    public List<TableEntity> queryList(TableEntity lookup) {

        StringBuffer sql = new StringBuffer("SELECT a.relname AS tableName, c.description AS comments,d.created AS createTime ");
        sql.append(from);

        List<Object> params = new ArrayList<>();

        sql.append(buildListWhere(lookup, params));
        // 排序
        if (!StringUtils.isAnyBlank(lookup.getSortColumn(), lookup.getSortOrder())
                && sortableColumns.contains(lookup.getSortColumn())) {
            sql.append(" order by ? ? ");
            params.add(lookup.getSortColumn());
            params.add(lookup.getSortOrder());
        } else {
            sql.append(" order by d.created DESC ");
        }
        // 分页
        String pageSql = SqlUtil.oracleLimit(sql.toString(), lookup.getPage(), lookup.getSize());
        return jdbcTemplate.query(pageSql, new BeanPropertyRowMapper<TableEntity>(TableEntity.class),
                params.toArray(new Object[0]));
    }

    /**
     * 构建where条件
     */
    private String buildListWhere(TableEntity lookup, List<Object> params) {
        StringBuilder sql = new StringBuilder();
        // 表名
        if (StringUtils.isNotBlank(lookup.getTableName())) {
            sql.append(" and a.relname like ? ");
            params.add("%" + lookup.getTableName().toUpperCase() + "%");
        }
        // 注释
        if (StringUtils.isNoneBlank(lookup.getComments())) {
            sql.append(" and c.description like ? ");
            params.add("%" + lookup.getComments().toUpperCase() + "%");
        }
        return sql.toString();

    }

    @Override
    public int countList(TableEntity lookup) {
        StringBuffer sql = new StringBuffer(
                "SELECT count(*) ").append(from);
        List<Object> params = new ArrayList<>();
        sql.append(buildListWhere(lookup, params));
        Object[] args = params.toArray(new Object[0]);
        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, args);
        return count == null ? 0 : count;
    }

    @Override
    public List<ColumnEntity> queryColumns(String tableName) {
        String sql = "SELECT  a.attname AS columnName, t.typname AS dataType, col_description(a.attrelid, a.attnum) AS \"comments\",  c.contype AS constraint_type " +
                "FROM   pg_attribute a JOIN     pg_type t ON a.atttypid = t.oid " +
                "LEFT JOIN  pg_constraint c ON a.attrelid = c.conrelid AND a.attnum = ANY (c.conkey) " +
                "WHERE a.attrelid = (SELECT oid FROM pg_class WHERE relname = ?) AND a.attnum > 0";
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<ColumnEntity>(ColumnEntity.class), tableName);
    }

    @Override
    public TableEntity queryTable(String tableName) {
        String sql  = "SELECT a.relname AS tableName, c.description AS comments,d.created AS createTime " +
                from  + "and a.relname = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<TableEntity>(TableEntity.class), tableName);
    }
}
