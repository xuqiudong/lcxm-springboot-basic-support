package cn.xuqiudong.generator.dao;

import cn.xuqiudong.common.util.sql.SqlUtil;
import cn.xuqiudong.generator.contant.DatabaseType;
import cn.xuqiudong.generator.model.ColumnEntity;
import cn.xuqiudong.generator.model.TableEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述:
 * @author  Vic.xu
 * @since  2020年3月10日 上午11:19:20 TODO 编写实现代码
 */
public class OracleGeneratorDao extends BaseGeneratorDao {

    public OracleGeneratorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int countList(TableEntity lookup) {
        StringBuffer sql = new StringBuffer(
                "SELECT count(*) from user_tables a  left join user_tab_comments b on a.TABLE_NAME = b.table_name  "
                        + " left join user_objects c on a.TABLE_NAME = c.object_name and c.OBJECT_TYPE = 'TABLE' "
                        + " where a.TABLESPACE_NAME   is not null");
        List<Object> params = new ArrayList<Object>();
        sql.append(buildListWhere(lookup, params));
        Object[] args = params.toArray(new Object[0]);
        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, args);
        return count == null ? 0 : count;
    }

    /** 构建where条件 */
    private String buildListWhere(TableEntity lookup, List<Object> params) {
        StringBuffer sql = new StringBuffer();
        // 表名
        if (StringUtils.isNotBlank(lookup.getTableName())) {
            sql.append(" and a.table_name like ? ");
            params.add("%" + lookup.getTableName().toUpperCase() + "%");
        }

        // 注释
        if (StringUtils.isNoneBlank(lookup.getComments())) {
            sql.append(" and b.comments like ? ");
            params.add("%" + lookup.getComments().toUpperCase() + "%");
        }
        // 引擎 oracle 忽略
        return sql.toString();

    }

    @Override
    public List<TableEntity> queryList(TableEntity lookup) {
        StringBuffer sql =
                new StringBuffer("SELECT a.table_name as tableName, b.comments, 'none' as engine ,c.created as createTime "
                        + "     FROM user_tables a  left join user_tab_comments b on a.TABLE_NAME = b.table_name  "
                        + " left join user_objects c on a.table_name = c.object_name and c.object_type = 'TABLE' "
                        + " where a.tablespace_name   is not null ");
        List<Object> params = new ArrayList<Object>();

        sql.append(buildListWhere(lookup, params));
        // 排序
        if (!StringUtils.isAnyBlank(lookup.getSortColumn(), lookup.getSortOrder())) {
            sql.append(" order by ? ? ");
            params.add(lookup.getSortColumn());
            params.add(lookup.getSortOrder());
        } else {
            // sql.append(" order by c.created desc ");
            sql.append(" order by a.table_name ");
        }
        // 分页
        String pageSql = SqlUtil.oracleLimit(sql.toString(), lookup.getPage(), lookup.getSize());
        return jdbcTemplate.query(pageSql, new BeanPropertyRowMapper<TableEntity>(TableEntity.class),
                params.toArray(new Object[0]));
    }

    @Override
    public List<ColumnEntity> queryColumns(String tableName) {
        String sql =
                "   select a.column_name as  columnName, a.data_type as dataType, b.comments ,d.constraint_type as columnKey"
                        + " FROM user_tab_columns a left join user_col_comments  b on a.table_name = b.table_name and a.column_name = b.column_name "
                        + " LEFT JOIN user_cons_columns c on a.TABLE_NAME = c.table_name and c.column_name = a.COLUMN_NAME "
                        + " LEFT JOIN  user_constraints d on c.constraint_name = d.constraint_name"
                        + " WHERE a.table_name = ? order by a.column_id";
        List<ColumnEntity> list =
                jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<ColumnEntity>(ColumnEntity.class), tableName);
        // 重复列去掉 只保留主键的 )
        Map<String, List<ColumnEntity>> group =
                list.stream().collect(Collectors.groupingBy(ColumnEntity::getColumnName, LinkedHashMap::new, Collectors.toList()));
        List<ColumnEntity> result = new ArrayList<ColumnEntity>();
        group.forEach((k, v) -> {
            for (ColumnEntity c : v) {
                if (c.isPrimaryKey()) {
                    result.add(c);
                    return;
                }
            }
            result.add(v.get(0));
        });
        return result;
    }

    @Override
    public TableEntity queryTable(String tableName) {
        String sql = "SELECT a.table_name as tableName, b.comments, 'none' as engine ,c.created as createTime "
                + "     FROM user_tables a  left join user_tab_comments b on a.TABLE_NAME = b.table_name  "
                + " left join user_objects c on a.table_name = c.object_name and c.object_type = 'TABLE' "
                + "         where a.table_name = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<TableEntity>(TableEntity.class), tableName);

    }

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.oracle;
    }

}
