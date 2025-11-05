package cn.xuqiudong.generator.contant;

import cn.xuqiudong.generator.model.DataBaseDialectInfo;

/**
 * 数据库类型
 *
 * @author Vic.xu
 * @since 2021/10/22
 */
public enum DatabaseType {

    /** mysql */
    mysql(DataBaseDialectInfo.MYSQL),

    /** oracle */
    oracle(DataBaseDialectInfo.ORACLE),
    /**gauss*/
    gauss(DataBaseDialectInfo.GAUSS);

    /**
     * 不同数据库的一些sql方面的区别
     */
    final DataBaseDialectInfo dialect;


    DatabaseType(DataBaseDialectInfo dialect) {
        this.dialect = dialect;
    }

    public DataBaseDialectInfo getDialect() {
        return dialect;
    }

    public static DatabaseType getByName(String name) {
        for (DatabaseType databaseType : DatabaseType.values()) {
            if (databaseType.name().equals(name)) {
                return databaseType;
            }
        }
        return null;
    }
}
