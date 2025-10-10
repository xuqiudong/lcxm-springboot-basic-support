package cn.xuqiudong.basic.generator.registry;

import cn.xuqiudong.basic.generator.model.DataType;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 *  注册 持有数据类型映射: 数据库字段类型 对应 java 类型和 jdbc  类型
 *
 * @author Vic.xu
 * @since 2025-09-11 15:22
 */
public class DataTypeMappingRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTypeMappingRegistry.class);

    /**
     * 数据类型映射   map:  数据库数据类型  -->  DataTypeMapping (JdbcType,  JavaType)
     */
    private static Map<String, DataType> HOLDER = new HashMap<>();

    /**
     * 找不到的数据类型时的映射
     */
    private static final DataType DEFAULT_MAPPING = new DataType("", JdbcType.VARCHAR, String.class, false);


    /**
     * 注册数据类型映射
     */
    public static void register(String columnType, JdbcType jdbcType, Class<?> javaType) {
        Assert.hasText(columnType, "columnType can not be null");
        register(new DataType(columnType, jdbcType, javaType, false));
    }

    public static void register(String columnType, JdbcType jdbcType, Class<?> javaType, boolean lob) {
        Assert.hasText(columnType, "columnType can not be null");
        register(new DataType(columnType, jdbcType, javaType, lob));
    }

    /**
     * 注册数据类型映射
     */
    public static void register(DataType mapping) {
        Assert.notNull(mapping, "mapping can not be null");
        HOLDER.put(mapping.getColumnType().toLowerCase(), mapping);
    }

    /**
     * 获取数据类型映射, 获取不到则返回 默认映射
     */
    public static DataType get(String columnType) {
        Assert.hasText(columnType, "columnType can not be null");
        DataType mapping = HOLDER.get(columnType.toLowerCase());
        if (mapping == null) {
            LOGGER.warn("未找到数据类型映射: {}", columnType);
            return DEFAULT_MAPPING;
        }
        return mapping;
    }

    static {
        // ============================== 整数类型 ==============================
        // MySQL的tinyint类型，对应Java Integer
        register("tinyint", JdbcType.TINYINT, Integer.class);
        // 小整数类型，适用于MySQL的smallint、Oracle的NUMBER(5)以内
        register("smallint", JdbcType.SMALLINT, Integer.class);
        // MySQL特有mediumint类型
        register("mediumint", JdbcType.INTEGER, Integer.class);
        // 通用int类型
        register("int", JdbcType.INTEGER, Integer.class);
        register("integer", JdbcType.INTEGER, Integer.class);


        // 长整数类型，适用于MySQL的bigint、Oracle的NUMBER(19)以内
        register("bigint", JdbcType.BIGINT, Long.class);

        // ============================== 小数类型 ==============================
        // 单精度浮点类型
        register("float", JdbcType.FLOAT, Float.class);
        // 双精度浮点类型
        register("double", JdbcType.DOUBLE, Double.class);
        // 高精度小数类型（金额、税率等场景）
        register("decimal", JdbcType.DECIMAL, BigDecimal.class);
        register("numeric", JdbcType.DECIMAL, BigDecimal.class);
        // Oracle/GaussDB的整数类型  此处统一使用 BigDecimal
        register("number", JdbcType.DECIMAL, BigDecimal.class);
        // GaussDB/Oracle的精确整数类型

        // ============================== 字符串类型 ==============================
        // 定长字符串类型
        register("char", JdbcType.CHAR, String.class);
        // 变长字符串类型
        register("varchar", JdbcType.VARCHAR, String.class);
        // Oracle特有变长字符串
        register("varchar2", JdbcType.VARCHAR, String.class);
        // 支持Unicode的变长字符串
        register("nvarchar", JdbcType.NVARCHAR, String.class);
        register("nvarchar2", JdbcType.NVARCHAR, String.class);

        // 大文本类型（LOB类型）
        // 普通文本大字段
        register("text", JdbcType.LONGVARCHAR, String.class, true);
        // MySQL特有小文本大字段
        register("tinytext", JdbcType.LONGVARCHAR, String.class, true);
        // MySQL特有中文本大字段
        register("mediumtext", JdbcType.LONGVARCHAR, String.class, true);
        // MySQL特有长文本大字段
        register("longtext", JdbcType.LONGVARCHAR, String.class, true);
        // Oracle/GaussDB的字符大对象
        register("clob", JdbcType.CLOB, String.class, true);
        // Oracle的Unicode大对象
        register("nclob", JdbcType.NCLOB, String.class, true);

        // ============================== 二进制类型 ==============================
        // 二进制大对象（LOB类型）
        register("blob", JdbcType.BLOB, byte[].class, true);
        // MySQL特有长二进制大对象
        register("longblob", JdbcType.LONGVARBINARY, byte[].class, true);

        // ============================== 日期时间类型 ==============================
        // 仅日期类型（年-月-日）
        register("date", JdbcType.DATE, LocalDate.class);
        // 仅时间类型（时:分:秒）
        register("time", JdbcType.TIME, LocalTime.class);
        // 带时区的时间类型
        register("time with time zone", JdbcType.TIME_WITH_TIMEZONE, OffsetTime.class);
        // 日期+时间类型（不带时区）
        register("datetime", JdbcType.TIMESTAMP, LocalDateTime.class);
        // 通用时间戳类型（不带时区）
        register("timestamp", JdbcType.TIMESTAMP, LocalDateTime.class);
        // 带微秒的时间戳（不带时区）
        register("timestamp(6)", JdbcType.TIMESTAMP, LocalDateTime.class);
        // 带时区的时间戳（标准JDBC类型）
        register("timestamp with time zone", JdbcType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.class);

        // ============================== 特殊类型 ==============================
        // 布尔类型
        register("bit", JdbcType.BIT, Boolean.class);
        register("boolean", JdbcType.BOOLEAN, Boolean.class);
        // MySQL枚举类型
        register("enum", JdbcType.VARCHAR, String.class);
        // JSON类型（标准JDBC中用OTHER，非LOB类型）
        //  MySQL 5.7+ JSON类型
        register("json", JdbcType.OTHER, String.class);
        // PostgreSQL JSONB类型
        register("jsonb", JdbcType.OTHER, String.class);
        // Oracle JSON类型（大JSON用CLOB存储）
        register("json_clob", JdbcType.CLOB, String.class, true);
    }

}
