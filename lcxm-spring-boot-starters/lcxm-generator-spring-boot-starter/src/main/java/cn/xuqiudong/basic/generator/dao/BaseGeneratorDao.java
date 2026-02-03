package cn.xuqiudong.basic.generator.dao;

import cn.xuqiudong.basic.generator.enums.DatabaseType;
import cn.xuqiudong.basic.generator.model.meta.ColumnMeta;
import cn.xuqiudong.basic.generator.model.meta.TableMeta;
import cn.xuqiudong.basic.generator.model.query.TableLookup;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * 说明 :  查询DAO，本来准备使用MYBATIS的，但是改为Starter之后，不想引入额外的依赖
 *
 * @author Vic.xu
 * @since 2020年3月10日 上午11:11:35
 */
public abstract class BaseGeneratorDao {

    protected JdbcTemplate jdbcTemplate;

    /**
     * 数据库名称
     */
    public abstract DatabaseType databaseType();

    /**
     * 查询表列表
     */
    public abstract List<TableMeta> queryList(TableLookup lookup);

    /**
     * 查询表列表总数 用于分页
     */
    public abstract int countList(TableLookup lookup);

    /**
     * 查询表的所有列的列表
     */
    public abstract List<ColumnMeta> queryColumns(String tableName);

    /**
     * 查询表的详情
     */
    public abstract TableMeta queryTable(String tableName);

}
