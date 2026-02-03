package cn.xuqiudong.basic.generator.registry;

import cn.xuqiudong.basic.generator.dao.BaseGeneratorDao;
import cn.xuqiudong.basic.generator.dao.GaussGeneratorDao;
import cn.xuqiudong.basic.generator.dao.MysqlGeneratorDao;
import cn.xuqiudong.basic.generator.dao.OracleGeneratorDao;
import cn.xuqiudong.basic.generator.enums.DatabaseType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 描述:
 * 根据数据库类型获取合适的数据库访问dao
 *
 * @author Vic.xu
 * @since 2025-09-12 11:34
 */
public class GeneratorDaoProvider {

    /**
     * 持有 数据库访问dao
     */
    private static final Map<DatabaseType, Function<JdbcTemplate, BaseGeneratorDao>> REGISTRY = new HashMap<>();

    static {
        registerDefault();
    }

    /**
     * 默认注册
     */
    private static void registerDefault() {
        register(DatabaseType.oracle, OracleGeneratorDao::new);
        register(DatabaseType.mysql, MysqlGeneratorDao::new);
        register(DatabaseType.gauss, GaussGeneratorDao::new);
    }

    /**
     * 注册 数据库访问dao
     *
     * @param type    数据库类型
     * @param creator 创建器
     */
    public static void register(DatabaseType type, Function<JdbcTemplate, BaseGeneratorDao> creator) {
        REGISTRY.put(type, creator);
    }

    /**
     * 获取合适的数据库访问dao
     */
    public static BaseGeneratorDao buildDao(DatabaseType type, JdbcTemplate jdbcTemplate) {
        Assert.notNull(type, "database type can not be null");
        Assert.notNull(jdbcTemplate, "jdbcTemplate can not be null");
        Function<JdbcTemplate, BaseGeneratorDao> creator = REGISTRY.get(type);
        Assert.notNull(creator, "database type " + type + " not support");
        return creator.apply(jdbcTemplate);
    }
}
