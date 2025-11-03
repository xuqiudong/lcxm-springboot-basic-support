package cn.xuqiudong.basic.generator.model;

import cn.xuqiudong.basic.generator.registry.DataTypeMappingRegistry;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;


/**
 * 描述:
 * 数据库字段类型和java 类型以及JdbcType 的映射关系
 *
 * @author Vic.xu
 * @see DataTypeMappingRegistry
 * @since 2025-09-11 15:18
 */
@Data
public class DataType {

    /**
     * 数据库中定义的字段类型
     */
    private String columnType;

    /**
     * Jdbc类型
     */
    private JdbcType jdbcType;

    /**
     * java类型
     */
    private Class<?> javaType;

    /**
     * 是否大文本字段
     */
    private boolean lob;

    public DataType(String columnType, JdbcType jdbcType, Class<?> javaType, boolean lob) {
        this.columnType = columnType;
        this.jdbcType = jdbcType;
        this.javaType = javaType;
        this.lob = lob;
    }
}
