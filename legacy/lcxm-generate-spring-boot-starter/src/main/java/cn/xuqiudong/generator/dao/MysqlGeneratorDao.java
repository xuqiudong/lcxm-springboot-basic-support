package cn.xuqiudong.generator.dao;

import cn.xuqiudong.generator.contant.DatabaseType;
import cn.xuqiudong.generator.model.ColumnEntity;
import cn.xuqiudong.generator.model.TableEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * @author  Vic.xu
 * @since  2020年3月10日 上午11:19:20
 */
public class MysqlGeneratorDao extends BaseGeneratorDao {

    public MysqlGeneratorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int countList(TableEntity lookup) {
        StringBuffer sql = new StringBuffer(
                "SELECT count(*) FROM information_schema.tables " + "		WHERE table_schema = (select database())");
        List<Object> params = new ArrayList<Object>();
        sql.append(buildListWhere(lookup, params));

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(new Object[0]), Integer.class);
    }

    /** 构建where条件 */
    private String buildListWhere(TableEntity lookup, List<Object> params) {
        StringBuffer sql = new StringBuffer();
        // 表名
        if (StringUtils.isNotBlank(lookup.getTableName())) {
            sql.append(" and table_name like concat('%', ?, '%') ");
            params.add(lookup.getTableName());
        }

        // 注释
        if (StringUtils.isNoneBlank(lookup.getComments())) {
            sql.append(" and table_comment like concat('%',?, '%') ");
            params.add(lookup.getComments());
        }
        // 引擎
        if (StringUtils.isNotBlank(lookup.getEngine())) {
            sql.append(" and engine = ? ");
            params.add(lookup.getEngine());
        }
        return sql.toString();

    }

    @Override
    public List<TableEntity> queryList(TableEntity lookup) {
        StringBuffer sql =
                new StringBuffer("SELECT table_name tableName, engine, table_comment comments, create_time createTime "
                        + "		FROM information_schema.tables " + "		WHERE table_schema = (select database())");
        List<Object> params = new ArrayList<Object>();

        sql.append(buildListWhere(lookup, params));
        // 排序
        if (!StringUtils.isAnyBlank(lookup.getSortColumn(), lookup.getSortOrder())) {
            sql.append(" order by ? ? ");
            params.add(lookup.getSortColumn());
            params.add(lookup.getSortOrder());
        } else {
            sql.append(" order by create_time desc ");
        }
        // 分页
        sql.append(" LIMIT ").append(lookup.getIndex()).append(", ").append(lookup.getSize());
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<TableEntity>(TableEntity.class),
                params.toArray(new Object[0]));
    }

    @Override
    public List<ColumnEntity> queryColumns(String tableName) {
        String sql =
                "SELECT column_name columnName, data_type dataType, column_comment comments, column_key columnKey, extra \n"
                        + "		FROM information_schema.columns\n"
                        + " 		WHERE table_name = ? and table_schema = (select database()) order by ordinal_position";
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<ColumnEntity>(ColumnEntity.class),
                tableName);
    }

    @Override
    public TableEntity queryTable(String tableName) {
        String sql =
                "select table_name tableName, engine, table_comment comments, create_time createTime from information_schema.tables \n"
                        + "			where table_schema = (select database()) and table_name = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<TableEntity>(TableEntity.class), tableName);

    }

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.mysql;
    }

}
