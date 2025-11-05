package cn.xuqiudong.generator.dao;

import cn.xuqiudong.generator.contant.DatabaseType;
import cn.xuqiudong.generator.model.ColumnEntity;
import cn.xuqiudong.generator.model.TableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * 说明 :  查询DAO，本来准备使用MYBATIS的，但是改为Starter之后，不想引入额外的依赖
 * @author  Vic.xu
 * @since  2020年3月10日 上午11:11:35
 */
public abstract class BaseGeneratorDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * 数据库名称
     */
    public abstract DatabaseType databaseType();

    /**
     * 查询表列表
     */
    public abstract List<TableEntity> queryList(TableEntity lookup);

    /**
     * 查询表列表总数 用于分页
     */
    public abstract int countList(TableEntity lookup);

    /**
     * 查询表的所有列的列表
     */
    public abstract List<ColumnEntity> queryColumns(String tableName);

    /**
     * 查询表的详情
     */
    public abstract TableEntity queryTable(String tableName);

}
