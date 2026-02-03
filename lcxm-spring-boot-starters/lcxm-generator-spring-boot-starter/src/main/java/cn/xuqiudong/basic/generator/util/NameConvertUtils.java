package cn.xuqiudong.basic.generator.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Set;

/**
 * 描述:
 * 名称转换工具
 *
 * @author Vic.xu
 * @since 2025-09-13 10:03
 */
public class NameConvertUtils {


    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, Set<String> tablePrefix) {
        if (!CollectionUtils.isEmpty(tablePrefix)) {
            for (String tablePrefixItem : tablePrefix) {
                if (StringUtils.startsWithIgnoreCase(tableName, tablePrefixItem)) {
                    // 忽略大小写
                    tableName = ignoreCaseStartCut(tableName, tablePrefixItem);
                    break;
                }
            }
        }
        return columnToJava(tableName);
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            // 忽略大小写
            tableName = ignoreCaseStartCut(tableName, tablePrefix);
            // tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 把开头部分给截取掉，忽略大小写
     *
     * @param source source
     * @param start  start
     * @return case and  cut
     */
    private static String ignoreCaseStartCut(String source, String start) {
        if (StringUtils.startsWithIgnoreCase(source, start)) {
            return source.substring(start.length());
        }
        return source;
    }


    /**
     * 列名转换成Java属性名 is_delete -->delete
     */
    public static String columnToJava(String columnName) {
        // return WordUtils.capitalizeFully(columnName, new char[] { '_' }).replace("_", "");//aaBBcc-->aabbcc 不是我想要的
        return underlineToCamel(columnName);
    }

    /**
     * 下划线
     */
    static String UNDERLINE = "_";
    /**
     * 字段属性转java属性忽略的前缀
     */
    static String IGNORE_COLUMN_PREFIX = "is_";

    /**
     * 下划线转驼峰 但是不小写
     *
     * @param str 列
     * @return 驼峰
     */
    public static String underlineToCamel(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        int prefixLen = IGNORE_COLUMN_PREFIX.length();
        if (str.length() > prefixLen && str.substring(0, prefixLen).equalsIgnoreCase(IGNORE_COLUMN_PREFIX)) {
            str = str.substring(prefixLen);
        }
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (String s : str.split(UNDERLINE)) {
            sb.append(StringUtils.capitalize(s));
        }
        return sb.toString();
    }

    /**
     * 获取输出文件的相对路径: 把包名转换成路径 +  className + 后缀
     */
    public static String getOutputFilePath(String packageName, String className, String suffix) {
        return packageName.replace(".", File.separator) + File.separator + className + suffix;
    }

    /**
     * 获取完整的输出文件路径: 把包名转换成路径 +  className + 后缀
     */
    public static String getFullOutputFilePath(String output,
                                               String packageName, String className, String suffix) {
        String outputFilePath = getOutputFilePath(packageName, className, suffix);
        return output + File.separator + outputFilePath;
    }
}
