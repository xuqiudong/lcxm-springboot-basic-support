package cn.xuqiudong.basic.third.outbound.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 出站响应 JavaType 构建工具。
 *
 * <p>用于处理第三方常见的泛型响应结构，例如 {@code Wrapper<Data>}、
 * {@code Wrapper<List<Data>>}。</p>
 *
 * @author Vic.xu
 */
public final class ResponseTypeUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ResponseTypeUtils() {
    }

    /**
     * 构建普通泛型对象类型，例如 {@code ThirdResponse<OrderDTO>}。
     */
    public static JavaType objectType(Class<?> wrapper, Class<?> inner) {
        return MAPPER.getTypeFactory().constructParametricType(wrapper, inner);
    }

    /**
     * 构建 List 类型，例如 {@code List<OrderDTO>}。
     */
    public static JavaType listType(Class<?> element) {
        return MAPPER.getTypeFactory().constructCollectionType(List.class, element);
    }

    /**
     * 构建包装 List 类型，例如 {@code ThirdResponse<List<OrderDTO>>}。
     */
    public static JavaType wrapperListType(Class<?> wrapper, Class<?> element) {
        return MAPPER.getTypeFactory().constructParametricType(wrapper, listType(element));
    }

    /**
     * 构建 Map 类型，例如 {@code Map<String, OrderDTO>}。
     */
    public static JavaType mapType(Class<?> key, Class<?> value) {
        return MAPPER.getTypeFactory().constructMapType(Map.class, key, value);
    }

    /**
     * 构建任意泛型类型，适合更复杂的嵌套结构。
     */
    public static JavaType parametricType(Class<?> rawType, JavaType... subTypes) {
        return MAPPER.getTypeFactory().constructParametricType(rawType, subTypes);
    }
}
