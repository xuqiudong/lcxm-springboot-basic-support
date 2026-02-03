package cn.xuqiudong.basic.generator.dao;

import cn.xuqiudong.basic.generator.enums.DatabaseType;
import cn.xuqiudong.basic.generator.model.meta.ColumnMeta;
import cn.xuqiudong.basic.generator.model.meta.TableMeta;
import cn.xuqiudong.basic.generator.model.query.TableLookup;
import cn.xuqiudong.common.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述: oracle数据库元数据查询
 *
 * @author Vic.xu
 * @since 2020年3月10日 上午11:19:20
 */
public class OracleGeneratorDao extends BaseGeneratorDao {

    public OracleGeneratorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int countList(TableLookup lookup) {
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

    /**
     * 构建where条件
     */
    private String buildListWhere(TableLookup lookup, List<Object> params) {
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
    public List<TableMeta> queryList(TableLookup lookup) {
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
        return jdbcTemplate.query(pageSql, new BeanPropertyRowMapper<>(TableMeta.class),
                params.toArray(new Object[0]));
    }

    @Override
    public List<ColumnMeta> queryColumns(String tableName) {
        String sql =
                "   select a.column_name as  columnName, a.data_type as dataType, b.comments ,d.constraint_type as columnKey"
                        + " FROM user_tab_columns a left join user_col_comments  b on a.table_name = b.table_name and a.column_name = b.column_name "
                        + " LEFT JOIN user_cons_columns c on a.TABLE_NAME = c.table_name and c.column_name = a.COLUMN_NAME "
                        + " LEFT JOIN  user_constraints d on c.constraint_name = d.constraint_name"
                        + " WHERE a.table_name = ? order by a.column_id";
        List<ColumnMeta> list =
                jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(ColumnMeta.class), tableName);
        // 重复列去掉 只保留主键的 ) TODO 有没有可能是查询错了
        Map<String, List<ColumnMeta>> group =
                list.stream().collect(Collectors.groupingBy(ColumnMeta::getColumnName, LinkedHashMap::new, Collectors.toList()));
        List<ColumnMeta> result = new ArrayList<>();
        group.forEach((k, v) -> {
            for (ColumnMeta c : v) {
                if (c.isPk()) {
                    result.add(c);
                    return;
                }
            }
            result.add(v.get(0));
        });
        return result;
    }

    @Override
    public TableMeta queryTable(String tableName) {
        String sql = "SELECT a.table_name as tableName, b.comments, 'none' as engine ,c.created as createTime "
                + "     FROM user_tables a  left join user_tab_comments b on a.TABLE_NAME = b.table_name  "
                + " left join user_objects c on a.table_name = c.object_name and c.object_type = 'TABLE' "
                + "         where a.table_name = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(TableMeta.class), tableName);

    }

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.oracle;
    }

}
