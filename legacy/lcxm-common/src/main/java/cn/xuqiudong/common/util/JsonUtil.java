package cn.xuqiudong.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 说明 :
 * @author  Vic.xu
 * @since  2019年11月15日 下午5:40:53
 */
public class JsonUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 不设置额外的属性的 ObjectMapper
     */
    public static final ObjectMapper PURE_OBJECT_MAPPER = new ObjectMapper();

    static {
        //  空属性不序列化
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //如果JSON中有新增的字段并且是实体类类中不存在的，不报错
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 提供对 Java 8 java.time 日期/时间 API 的支持，例如 LocalDate、LocalDateTime、ZonedDateTime 等
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 将json转换成对象Class
     * @param src
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String src, Class<T> clazz) {
        if (StringUtils.isEmpty(src) || clazz == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(src, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将JSON转换成对象 TypeReference方式
     * @param src
     * @param typeReference
     * EG: 转单个对象的时候  类似new TypeReference<User>(){}
     * EG: 转list对象的时候 类似 new TypeReference<List<User>>() {}
     * EG: 转map对象jsonToObject(json, new TypeReference<Map<String,User>>() {});  
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String src, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(src) || typeReference == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(src, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将json转换成对象
     * @param src
     * @param collectionClass
     * @param elementClasses
     * @param <T>
     * EG:  转list对象  jsonToObject(json, List.class,User.class)
     * EG:  转map对象    jsonToObject(json, Map.class,String.class,User.class);
     * @return
     */
    public static <T> T jsonToObject(String src, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return OBJECT_MAPPER.readValue(src, javaType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对象转JSON String
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 对象转JSON String, 保留null值的属性
     */
    public static String toJsonWithAllFields(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return PURE_OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对象转JSON String 格式化的
     * @param object
     * @return
     */
    public static String toJsonPretty(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void printJson(Object object) {
        if (object == null) {
            return;
        }
        System.out.println(object.getClass().getSimpleName() + "\n" + toJsonPretty(object));
    }

    public static void printJson(List<?> list) {
        Optional.ofNullable(list).orElse(new ArrayList<>()).forEach(o -> {
            printJson(o);
        });
    }
}
