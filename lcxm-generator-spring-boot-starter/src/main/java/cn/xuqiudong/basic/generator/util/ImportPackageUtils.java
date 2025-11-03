package cn.xuqiudong.basic.generator.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 描述:
 * 导包 工具类
 *
 * @author Vic.xu
 * @since 2025-09-11 17:07
 */
public class ImportPackageUtils {

    private static final String FORMAT = "%s;";

    /**
     * 判断类型是否需要导入
     */
    public static boolean needImport(Class<?> clazz) {
        // 1. 基础类型无需导入（如int、boolean）
        if (clazz.isPrimitive()) {
            return false;
        }
        // 2. 包装类型的原生类型（如Integer对应int）也无需特殊处理
        // 3. java.lang包下的类型无需导入（如String、Integer）
        if (clazz.getName().startsWith("java.lang.")) {
            return false;
        }
        // 4. 其他类型需要导入（如LocalDateTime、BigDecimal）
        return true;
    }

    /**
     * 获取导包字符串: 形如 import java.time.LocalTime;
     */
    public static String getImport(Class<?> clazz) {
        if (!needImport(clazz)) {
            return "";
        }
        return String.format(FORMAT, clazz.getName());
    }
    /**
     * 获取导包字符串: 形如 import java.time.LocalTime;
     */
    public static String getImport(String className) {
        if (StringUtils.isBlank( className)) {
            return "";
        }
        return String.format(FORMAT, className);
    }

    /**
     * 获取导包字符串Set:
     */
    public static Set<String> getImport(List<Class<?>> clazzList) {
        if (clazzList == null) {
            return new HashSet<>();
        }
        return clazzList.stream()
                .filter(ImportPackageUtils::needImport)
                .map(ImportPackageUtils::getImport)
                .collect(Collectors.toSet());
    }

}
