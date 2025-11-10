package cn.xuqiudong.common.util;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.common.annotation.QueryCondition;
import cn.xuqiudong.common.builder.WrapperBuilder;
import cn.xuqiudong.common.enums.QueryOperation;
import cn.xuqiudong.common.model.QueryFieldModel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述:
 * 解析包含QueryCondition注解的查询对象  最终构建QueryWrapper
 *
 * @author Vic.xu
 * @since 2025-10-29 15:42
 */
public class QueryConditionUtils {


    /**
     * 可能是直接 相等比较的字段 、或字段前缀 后缀集合， 比如id, uuid   xxx_id, _uuid
     */
    public static final Set<String> POSSIBLE_EQUAL_QUERY_FIELDS = Set.of("id", "uuid",
            "pid", "parentId",
            "status", "type",
            "category");

    /**
     * 缓存查询字段: class -> List<QueryFieldModel> (带QueryCondition注解的列集合 )
     */
    private static final Map<String, List<QueryFieldModel>> QUERY_FIELD_MODEL_CACHE = new ConcurrentHashMap<>();


    /**
     * 通过 包含QueryCondition注解 的对象 创建查询Wrapper
     */
    public static <T> QueryWrapper<T> createWrapper(Object query) {
        return builder(query).toWrapper();
    }

    /**
     * 通过 带QueryCondition注解的对象 创建查询WrapperBuilder
     */
    public static WrapperBuilder builder(Object query) {
        Class<?> queryClass = query.getClass();
        String key = queryClass.getName();
        List<QueryFieldModel> queryFields = QUERY_FIELD_MODEL_CACHE.computeIfAbsent(key, (k) -> getQueryFields(queryClass));

        WrapperBuilder wrapperBuilder = WrapperBuilder.create();
        for (QueryFieldModel queryField : queryFields) {
            Object value = queryField.getValue(query);
            // 判断当前字段值是否为null:
            boolean isNullValue = isNullValue(value);
            if (isNullValue) {
                // 但是允许查询 is null 的情况， 则添加is null 条件
                if (queryField.isAllowNullQuery()) {
                    wrapperBuilder.addCondition(queryField.getColumns()[0], QueryOperation.IS_NULL, null);
                }
                // 如果字段值为null  则不会走后续处理
                continue;
            }

            // 如果是字符串
            if (value instanceof String) {
                // 是否需要trim
                if (queryField.isTrim()) {
                    value = ((String) value).trim();
                }
                // 是否需要分割字符串： 当前操作是否需要分割字符串
                if (queryField.getOperation().isMultiValue()) {
                    value = StrUtil.split((String) value, queryField.getDelimiter());
                }
            }
            inferCondition(wrapperBuilder, queryField, value);
        }
        return wrapperBuilder;
    }


    /**
     * 根据字段类型推断 mybatis 的比较类型
     */
    public static QueryOperation inferQueryOperation(Class<?> fieldType) {
        if (fieldType == String.class) {
            return QueryOperation.LIKE;
        }
        if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
            return QueryOperation.IN;
        }
        return QueryOperation.EQ;
    }


    /**
     * 根据字段类型 和字段名称推断 mybatis 的比较类型
     */
    public static QueryOperation inferQueryOperation(Class<?> fieldType, String columnName) {
        if (fieldType == String.class) {
            if (isPossibleEqualQueryField(columnName)) {
                return QueryOperation.EQ;
            }
            return QueryOperation.LIKE;
        }
        if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray()) {
            return QueryOperation.IN;
        }
        return QueryOperation.EQ;
    }

    public static boolean isPossibleEqualQueryField(String columnName) {
        for (String field : POSSIBLE_EQUAL_QUERY_FIELDS) {
            boolean eq = field.equalsIgnoreCase( columnName)
                    || StringUtils.startsWithIgnoreCase(columnName, field)
                    || StringUtils.endsWithIgnoreCase(columnName, field);
            if (eq) {
                return true;
            }
        }
        return false;
    }

    /**
     * 推算查询条件
     */
    private static void inferCondition(WrapperBuilder wrapperBuilder, QueryFieldModel queryField, Object value) {
        String[] columns = queryField.getColumns();
        QueryOperation operation = queryField.getOperation();
        if (columns.length == 1) {
            wrapperBuilder.addCondition(columns[0], operation, value);
        }
        if (columns.length > 1) {
            wrapperBuilder.addNestedOrCondition(columns, operation, value);
        }

    }

    /**
     * 判断字符串是否为null, 包括是空字符串 , 如说是集合和数组 则判断长度是0
     */
    private static boolean isNullValue(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return StringUtils.isBlank((String) value);
        }
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        if (value.getClass().isArray()) {
            return ArrayUtil.isEmpty(value);
        }
        return false;
    }

    /**
     * 获取查询字段, 只操作 QueryCondition 标注的字段
     */
    private static List<QueryFieldModel> getQueryFields(Class<?> queryClass) {
        List<QueryFieldModel> queryFieldModels = new ArrayList<>();
        BeanDesc beanDesc = BeanUtil.getBeanDesc(queryClass);

        for (PropDesc propDesc : beanDesc.getProps()) {
            Field field = propDesc.getField();
            if (field == null || !field.isAnnotationPresent(QueryCondition.class)) {
                continue;
            }
            QueryCondition queryCondition = field.getAnnotation(QueryCondition.class);
            // 忽略字段
            if (queryCondition.ignore()) {
                continue;
            }
            Class<?> type = field.getType();
            queryFieldModels.add(new QueryFieldModel(queryCondition, type, propDesc));
        }
        return queryFieldModels;

    }


}
