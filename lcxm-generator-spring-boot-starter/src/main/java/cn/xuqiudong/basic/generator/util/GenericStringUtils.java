package cn.xuqiudong.basic.generator.util;

import cn.xuqiudong.basic.generator.dao.BaseGeneratorDao;
import cn.xuqiudong.basic.generator.model.meta.TableMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 *   把class 泛型类列表转化为泛型字符串 形如: <Integer>, <User, UserMapper>等
 * @author Vic.xu
 * @since 2025-09-19 13:45
 */
public class GenericStringUtils {

    /** 支持 List<Class<?>> */
    public static String toGenericString(List<Class<?>> classes) {
        if (classes == null || classes.isEmpty()) {
            return "";
        }
        return classes.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", ", "<", ">"));
    }

    /** 支持可变参数 Class<?>... */
    public static String toGenericString(Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            return "";
        }
        return Arrays.stream(classes)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", ", "<", ">"));
    }

    public static void main(String[] args) {
        // 测试 List
        List<Class<?>> list = List.of(BaseGeneratorDao.class, Integer.class);
        System.out.println(toGenericString(list));  // 输出: <User, BaseMapper>

        // 测试可变参数
        System.out.println(toGenericString(String.class, TableMeta.class)); // 输出: <User, BaseMapper>
    }

}
