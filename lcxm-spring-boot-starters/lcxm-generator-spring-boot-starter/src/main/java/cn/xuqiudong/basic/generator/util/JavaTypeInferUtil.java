package cn.xuqiudong.basic.generator.util;

import cn.xuqiudong.basic.generator.model.DataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;

import java.util.Set;

/**
 * 描述:
 * 推断java类型
 *
 * @author Vic.xu
 * @since 2025-11-11 13:38
 */
public class JavaTypeInferUtil {

    /**
     * 是否是boolean 类型的注释说明
     */
    private static final Set<String> BOOLEAN_COMMENT = Set.of("是否");

    /**
     * 推导java类型
     *
     * @param comments 列注释
     */
    public static DataType inferJavaType(DataType dataType, String comments) {
        if (isBoolean(dataType, comments)) {
            dataType = dataType.copy();
            dataType.setJavaType(Boolean.class);
        }
        return dataType;
    }

    /**
     * 1.根据注释判断是否是boolean
     * 2. 字段类型
     */
    public static boolean isBoolean(DataType dataType, String comments) {
        boolean isBoolean = false;
        // 判断注释
        for (String comment : BOOLEAN_COMMENT) {
            if (StringUtils.contains(comments, comment)) {
                isBoolean = true;
                break;
            }
        }
        if (!isBoolean) {
            return false;
        }

        // 判断字段类型 是否可以对应 boolean
        JdbcType jdbcType = dataType.getJdbcType();
        switch (jdbcType) {
            case BIT:
            case BOOLEAN:
            case TINYINT:
            case SMALLINT:
            case INTEGER:
                // Oracle的NUMBER类型对应这个
            case NUMERIC:
                // 精确小数类型，也用于Oracle的NUMBER
            case DECIMAL:
                return true;
            default:
                return false;
        }
    }
}
