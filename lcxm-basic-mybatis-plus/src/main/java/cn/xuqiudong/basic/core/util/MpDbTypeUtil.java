package cn.xuqiudong.basic.core.util;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * 描述:
 *    获取 mybatis plus 数据库类型
 * @author Vic.xu
 * @since 2025-10-27 11:12
 */
public class MpDbTypeUtil {

    /**
     * 获取数据库类型
     */
    public static DbType getDbType(String driverName) {
        DbType dbType;
        try {
            // 通过名称匹配（忽略大小写或直接匹配）
            dbType = DbType.getDbType(driverName);
        } catch (IllegalArgumentException e) {
            //  处理匹配失败的情况（特殊类型手动映射）
            dbType = mapSpecialDbType(driverName);
        }
        return dbType;
    }

    /**
     * 处理名称不直接匹配的特殊数据库类型
     */
    public static  DbType mapSpecialDbType(String driverName) {
        // 可选：抛出异常提示不支持，或返回 null 让插件自动推断
        return switch (driverName) {
            case "POSTGRESQL" -> DbType.POSTGRE_SQL;
            case "SQLSERVER" -> DbType.SQL_SERVER;
            case "H2" -> DbType.H2;
            // 其他特殊类型补充在这里
            default -> throw new IllegalArgumentException("不支持的数据库类型: " + driverName);
        };

    }
}
