package cn.xuqiudong.basic.generator.util;

import cn.xuqiudong.basic.generator.model.DataType;

/**
 * Description:
 * 类型转换工具类
 *
 * @author Vic.xu
 * @since 2026-05-13 10:33
 */
public class TypeConvertUtil {

    /**
     * java类型 转 typescript类型
     *
     * @param dataType java类型
     * @return typescript类型
     */
    public static String toTsType(DataType dataType) {
        if (dataType == null || dataType.getJavaType() == null) {
            return "any";
        }
        String javaSimpleTypeName = dataType.getJavaType().getSimpleName();
        if (javaSimpleTypeName == null) {
            return "any";
        }
        return switch (javaSimpleTypeName.trim()) {
            // 数字
            case "Byte", "Short", "Integer", "Long", "Float", "Double", "BigDecimal" -> "number";
            // 布尔
            case "Boolean" -> "boolean";
            // 字符串
            case "String" -> "string";
            // 日期
            case "LocalDate", "LocalDateTime", "Date", "Timestamp", "LocalTime" -> "string";
            // 默认 string
            default -> "any";
        };
    }

    /**
     * 根据java类型获取TS 类型的默认值
     *
     * @param dataType java类型
     */
    public static String getTsDefault(DataType dataType) {
        String tsType = toTsType(dataType);
        return switch (tsType) {
            case "number" -> "0";
            case "boolean" -> "false";
            case "string" -> "''";
            default -> "null";
        };
    }
}
