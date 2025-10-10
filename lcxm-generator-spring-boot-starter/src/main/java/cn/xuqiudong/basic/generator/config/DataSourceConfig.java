package cn.xuqiudong.basic.generator.config;

import cn.xuqiudong.basic.generator.registry.GeneratorDaoProvider;
import cn.xuqiudong.basic.generator.dao.BaseGeneratorDao;
import cn.xuqiudong.basic.generator.enums.DatabaseType;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.Assert;

/**
 * 描述:
 * 数据库连接配置(main函数生成的时候使用)
 *
 * @author Vic.xu
 * @since 2025-09-12 8:55
 */
public class DataSourceConfig {

    private String url;

    private String username;

    private String password;

    private Class<? extends java.sql.Driver> driverClass;

    @Getter
    private DatabaseType databaseType;

    private BaseGeneratorDao dao;


    public BaseGeneratorDao getDao() {
        if (dao == null) {
            dao = GeneratorDaoProvider.buildDao(databaseType, getJdbcTemplate());
        }
        Assert.notNull(dao, "generator dao is null,  please choose a suitable databaseType");
        return dao;
    }

    /**
     * 获取JdbcTemplate
     */
    private JdbcTemplate getJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
        if (driverClass != null) {
            dataSource.setDriverClassName(driverClass.getName());
        }
        return new JdbcTemplate(dataSource);
    }


    public static class Builder implements IConfigBuilder<DataSourceConfig> {
        final DataSourceConfig dataSourceConfig;

        private Builder() {
            this.dataSourceConfig = new DataSourceConfig();
        }

        public Builder(DatabaseType databaseType, String url, String username, String password) {
            this();
            Assert.notNull(databaseType, "databaseType can not be null");
            Assert.hasText(url, "url can not be null");
            this.dataSourceConfig.databaseType = databaseType;
            this.dataSourceConfig.url = url;
            this.dataSourceConfig.username = username;
            this.dataSourceConfig.password = password;
        }

        public Builder setDriverClass(Class<? extends java.sql.Driver> driverClass) {
            this.dataSourceConfig.driverClass = driverClass;
            return this;
        }

        @Override
        public DataSourceConfig build() {
            return dataSourceConfig;
        }

    }

}
