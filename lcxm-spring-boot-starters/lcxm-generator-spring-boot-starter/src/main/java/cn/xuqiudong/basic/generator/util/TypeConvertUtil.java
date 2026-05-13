package cn.xuqiudong.basic.generator.util;

/**
 * Description:
 *   类型转换工具类
 * @author Vic.xu
 * @since 2026-05-13 10:33
 */
public class TypeConvertUtil {

    /**
     * java类型 转 typescript类型
     * @param javaSimpleTypeName java类型
     * @return typescript类型
     */
    public static String toTsType(String javaSimpleTypeName) {
        if (javaSimpleTypeName == null) return "any";

        return switch (javaSimpleTypeName.trim()) {
            // 数字
            case "Byte", "Short", "Integer", "Long", "Float", "Double", "BigDecimal"
                    -> "number";
            // 布尔
            case "Boolean" -> "boolean";
            // 字符串
            case "String" -> "string";
            // 日期
            case "LocalDate", "LocalDateTime", "Date", "Timestamp" -> "string";
            // 默认 string
            default -> "any";
        };
    }
}
