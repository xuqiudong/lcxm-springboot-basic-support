package cn.xuqiudong.basic.core.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 描述: 集合工具类
 * @author Vic.xu
 * @since 2022-05-12 13:20
 */
@SuppressFBWarnings(value = "ICAST_INTEGER_MULTIPLY_CAST_TO_LONG")
public class ListUtils {

    /**
     * 切割list
     * @param list 原list
     * @param size 切割后的每个list的size
     * @param <T> 泛型
     * @return List<List < T>>
     */

    public static <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        if (list.size() <= size) {
            result.add(list);
            return result;
        }
        int limit = (list.size() + size - 1) / size;
        result = Stream.iterate(
                0, n -> n + 1).limit(limit).parallel().map(
                a -> list.stream().skip(a * size).limit(size).parallel()
                        .collect(Collectors.toList())).collect(Collectors.toList());
        return result;
    }

}
