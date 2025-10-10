package cn.xuqiudong.generator.main;

import cn.xuqiudong.basic.generator.config.DataSourceConfig;
import cn.xuqiudong.basic.generator.dao.BaseGeneratorDao;
import cn.xuqiudong.basic.generator.enums.DatabaseType;
import cn.xuqiudong.basic.generator.model.meta.TableMeta;
import cn.xuqiudong.basic.generator.model.query.TableLookup;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-09-12 15:54
 */
public class DatasourceConfigTest {


    @Test
    public void testMysql() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(DatabaseType.mysql,
                "jdbc:mysql://localhost:3306/qiudong?useSSL=false&serverTimezone=UTC",
                "qiudong",
                "qiudong12345678")
                .build();
        BaseGeneratorDao dao = dataSourceConfig.getDao();
        List<TableMeta> list = dao.queryList(new TableLookup());
        for (TableMeta meta : list) {
            System.out.println(meta);
        }
    }

    @Test
    public void testOracle() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(DatabaseType.oracle,
                "jdbc:oracle:thin:@192.168.80.68:1521:ORCL",
                "op",
                "op")
                .build();
        BaseGeneratorDao dao = dataSourceConfig.getDao();
        List<TableMeta> list = dao.queryList(new TableLookup());
        for (TableMeta meta : list) {
            System.out.println(meta);
        }
    }

    @Test
    public void testGauss() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(DatabaseType.gauss,
                "jdbc:postgresql://192.168.80.111:30100/oplease",
                "root",
                "Gauss@12")
                .build();
        BaseGeneratorDao dao = dataSourceConfig.getDao();
        List<TableMeta> list = dao.queryList(new TableLookup());
        for (TableMeta meta : list) {
            System.out.println(meta);
        }
    }


}
