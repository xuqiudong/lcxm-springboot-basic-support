/**
 *
 */
package cn.xuqiudong.common.util.reflect.compare;

import cn.xuqiudong.common.util.JsonUtil;
import cn.xuqiudong.common.util.collections.CalcDiffCollection;
import cn.xuqiudong.common.util.reflect.compare.annotations.CompareClassConfig;
import cn.xuqiudong.common.util.reflect.compare.annotations.CompareClassConfig.InnerType;
import cn.xuqiudong.common.util.reflect.compare.model.CompareClassResultModel;
import cn.xuqiudong.common.util.reflect.compare.model.SameAnnotionField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 说明 :  比较两个对象
 * @author Vic.xu
 * @since  2020年4月21日上午10:31:44
 */
@SuppressWarnings("PMD")
public class CompareClassUtil {
    /*
     * 粗略的需求:
     * 1. 单属性 直接比较
     * 2. 对象属性:
     *  2.1 级联对象: 比较id 记录name
     *  2.2 表单对象: 记录全部变化 --> 存在三级 则递归处理...............
     * 3. list对象属性
     *  3.1 级联对象: 比较id 记录name
     *  3.2 表单对象: 记录全部变化 -->  存在三级 则递归处理................
     *
     */

    private static final Logger logger = LoggerFactory.getLogger(CompareClassUtil.class);

    /** 递归深度 */
    protected static final int MAX_DEEP = 5;

    /**
     * 比较两个对象的属性 操作 <br />
     * @see  CompareClassConfig 参见此注解的详细说明
     * 类型说明：<br/>
     * <ul>
     * <li>添加： 空到非空</li>
     * <li>修改： 非空到非空</li>
     * <li>删除： 非空到空</li>
     * </ul>
     *
     * @param type   对象类型
     * @param source 原对象
     * @param target 目标对象
     * @throws IntrospectionException
     */
    public static <T> List<CompareClassResultModel> compare(Class<T> type, T source, T target) throws Exception {
        List<CompareClassResultModel> result = new ArrayList<CompareClassResultModel>();
        compare(result, type, source, target, 1);
        return result;
    }

    /**
     * 比较两个对象的属性 操作, 对于复杂对象只深入一层<br />
     * 类型说明：<br/>
     * <ul>
     * <li>添加： 空到非空</li>
     * <li>修改： 非空到非空</li>
     * <li>删除： 非空到空</li>
     * </ul>
     *
     * @param result 存放结果集
     * @param type   对象类型
     * @param source 原对象
     * @param target 目标对象
     * @param deep   深度 只展开二层以内 (obj.xxList 会展开xxList,obj.xxList.yyList不会展开到yyList)
     * @throws IntrospectionException
     */
    private static <T> void compare(List<CompareClassResultModel> result, Class<T> type, T source, T target, int deep)
            throws Exception {
        if (deep > MAX_DEEP) {
            // 超出了递归深度
            return;
        }
        if (type == null || source == null || target == null) {
            return;
        }

        Class<?> clazz = type;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            // 获得自身生命的各个属性 包含private
            Field[] fields = clazz.getDeclaredFields();
            loopField:
            for (Field field : fields) {
                // 只比较包含CompareClass注解的对象
                if (!field.isAnnotationPresent(CompareClassConfig.class)) {
                    continue loopField;
                }
                // jdk11 无法访问 com.sun.beans.introspect.PropertyInfo
                //PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = getReadMethod(clazz, field);
                String fieldName = field.getName();
                if (getMethod == null) {
                    logger.info("找不到{}类的{}字段的get方法", clazz.getSimpleName(), fieldName);
                    continue loopField;
                }
                CompareClassConfig config = field.getAnnotation(CompareClassConfig.class);
                // 字段描述说明
                InnerType innerType = config.compareInner();
                // 原对象中的字段值
                Object sourceValue = getMethod.invoke(source);
                // 现对象中的字段值
                Object targetValue = getMethod.invoke(target);
                switch (innerType) {
                    // 不展开比较
                    case NONE:
                        handlerNone(result, sourceValue, targetValue, config, fieldName);
                        break;
                    // 比较单个对象 只比较'id', 记录'name'变化
                    case CASCADE_SINGLE:
                        handlerCascadeSingle(result, type, field, sourceValue, targetValue, config);
                        break;
                    // 比较list对象 只循环比较'id', 记录'name'变化
                    case CASCADE_LIST:
                        handlerCascadeList(result, type, field, sourceValue, targetValue, config);
                        break;
                    // 单个对象, 需要展开对象内部进行比较
                    case SINGLE:
                        handlerSingle(result, type, field, sourceValue, targetValue, config, deep);
                        break;
                    // list对象, 需要展开对象内部进行比较
                    case LIST:
                        handlerList(result, type, field, sourceValue, targetValue, config, deep);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 比较整个子对象， 展开比较
     *
     * @param result
     * @param main
     * @param field
     * @param source
     * @param target
     * @param config
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void handlerSingle(List<CompareClassResultModel> result, Class<?> main, Field field, Object source,
                                      Object target, CompareClassConfig config, int deep) throws Exception {
        logger.info("展开比较整个{}的子对象{},", main.getSimpleName(), field.getName());
        Class clazz = field.getType();
        deep++;

        source = instanceIfNull(source, clazz);
        target = instanceIfNull(target, clazz);
        compare(result, clazz, source, target, deep);
    }

    //是null就构造一个新的对象
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object instanceIfNull(Object source, Class clazz) throws Exception {
        // jdk11 已经没有clazz.newInstance()了
        return source == null ? clazz.getDeclaredConstructor().newInstance() : source;
    }

    /**
     * 比较list类型的子对象， 展开比较
     * @param result
     * @param main
     * @param field
     * @param source
     * @param target
     * @param config
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void handlerList(List<CompareClassResultModel> result, Class<?> main, Field field, Object source,
                                    Object target, CompareClassConfig config, int deep) throws Exception {
        deep++;
        Class actualTypeArgument = findListFieldGenericType(field);
        // 获取 对象的id-->对象 的map
        Map<String, Object> sourceMap = list2Map((List<?>) source, actualTypeArgument, config.innerIdentifyField(),
                null);
        Map<String, Object> targetMap = list2Map((List<?>) target, actualTypeArgument, config.innerIdentifyField(),
                null);
        CalcDiffCollection<String> diff = CalcDiffCollection.instance(sourceMap.keySet(), targetMap.keySet());
        // 整个对象都是新增的，则一直到它的子属性 都是新增的  则只处理targetMap中的新增的对象标记位ADD
        List<String> addIds = diff.getOnlyInNew();
        for (String id : addIds) {
            List<SameAnnotionField> addGroup = findByAnnotation(targetMap.get(id), actualTypeArgument, deep, field.getName(), config.value());
            addSameGroupResult(result, addGroup, CompareResultEnum.ADD);
        }
        // 整个对象都是删除的,则一直到它的子属性 都是删除的  则只处理sourceMap中的新增的对象标记位DELETE
        List<String> delIds = diff.getOnlyInOld();
        for (String id : delIds) {
            List<SameAnnotionField> addGroup = findByAnnotation(sourceMap.get(id), actualTypeArgument, deep, field.getName(), config.value());
            addSameGroupResult(result, addGroup, CompareResultEnum.DELETE);
        }
        // 交集中可能有变化的
        List<String> unionIds = diff.getUnion();
        for (String id : unionIds) {
            Object sourceValue = sourceMap.get(id);
            Object targetValue = targetMap.get(id);
            compare(result, actualTypeArgument, sourceValue, targetValue, deep);

        }
    }

    /**
     * 处理List对象属性的比较 只比较 'id' 记录name
     *
     * @throws Exception
     */
    private static void handlerCascadeList(List<CompareClassResultModel> result, Class<?> main, Field field,
                                           Object source, Object target, CompareClassConfig config) throws Exception {
        // 确认数据类型为list
        if (!(source instanceof List) || !List.class.isAssignableFrom(field.getType())) {
            logger.info("{}对象属性{} 非为list集合", main.getSimpleName(), config.value());
            return;
        }
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            logger.info("{}对象的List类型的{}属性必须包含泛型信息", main.getSimpleName(), field.getName());
            return;
        }
        // 泛型类型
        ParameterizedType parameterizedType = (ParameterizedType) type;
        // 泛型的真实class类型
        Class<?> actualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        String fieldName = field.getName();
        String fieldDesc = config.value();
        Map<String, Object> sourceMap = list2Map((List<?>) source, actualTypeArgument, config.innerIdentifyField(),
                config.innerRecordShowField());
        Map<String, Object> targetMap = list2Map((List<?>) target, actualTypeArgument, config.innerIdentifyField(),
                config.innerRecordShowField());

        CalcDiffCollection<String> diff = CalcDiffCollection.instance(sourceMap.keySet(), targetMap.keySet());
        // 新增的
        List<String> addIds = diff.getOnlyInNew();
        for (String id : addIds) {
            result.add(new CompareClassResultModel(fieldName, fieldDesc, CompareResultEnum.ADD, targetMap.get(id), ""));
        }
        // 删除的
        List<String> delIds = diff.getOnlyInOld();
        for (String id : delIds) {
            result.add(
                    new CompareClassResultModel(fieldName, fieldDesc, CompareResultEnum.DELETE, "", sourceMap.get(id)));
        }
        // 交集中可能有变化的
        List<String> unionIds = diff.getUnion();
        for (String id : unionIds) {
            Object sourceValue = sourceMap.get(id);
            Object targetValue = targetMap.get(id);
            CompareResultEnum resultEnum = compareValue(sourceValue, targetValue);
            // 无变化的不管, 有变化 则记录
            if (CompareResultEnum.NONE != resultEnum) {
                result.add(new CompareClassResultModel(fieldName, fieldDesc, CompareResultEnum.MODIFY,
                        sourceMap.get(id), targetMap.get(id)));
            }

        }
    }

    /**
     * 获取field 为List 属性的泛型
     * @param field
     * @return
     */
    private static Class<?> findListFieldGenericType(Field field) {
        if (field == null) {
            return null;
        }
        if (!List.class.isAssignableFrom(field.getType())) {
            logger.info("{}属性非list集合， 请标注适合的注解", field.getName());
            return null;
        }
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            logger.info("对象的List类型的{}属性必须包含泛型信息", field.getName());
            return null;
        }
        // 泛型类型
        ParameterizedType parameterizedType = (ParameterizedType) type;
        // 泛型的真是class类型
        Class<?> actualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        return actualTypeArgument;
    }

    /**
     * 把相同组的数据转换并添加到结果集
     * @param result 存放的结果集
     * @param group  相同组的属性集
     * @param resultEnum 组类别 只应为新增 或者删除
     */
    private static void addSameGroupResult(List<CompareClassResultModel> result, List<SameAnnotionField> group, CompareResultEnum resultEnum) {
        if (CollectionUtils.isEmpty(group)) {
            return;
        }
        for (SameAnnotionField field : group) {
            result.add(new CompareClassResultModel(field, resultEnum));
        }
    }

    /**
     * 根据注解获取属性， 用于统一新增 统一删除的属性记录
     * @param obj 对象
     * @param clazz 对象类型
     * @param deep 深度
     * @param parentFieldNames 包含父类的字段属性
     * @param parentFieldDescs 包含父类的字段属性说明
     * @return
     * @throws Exception
     */
    private static List<SameAnnotionField> findByAnnotation(Object obj, Class<?> clazz, int deep, String parentFieldNames, String parentFieldDescs) throws Exception {
        List<SameAnnotionField> result = new ArrayList<SameAnnotionField>();
        findByAnnotation(result, obj, clazz, deep, parentFieldNames, parentFieldDescs);
        return result;
    }

    /**
     * 单个对象属性的：根据注解获取属性， 用于统一新增 统一删除的属性记录； 存在回调 注意代码
     * @param result
     * @param obj
     * @param clazz
     * @param deep
     * @param parentFieldNames
     * @param parentFieldDescs
     * @throws Exception
     */
    private static void findByAnnotation(List<SameAnnotionField> result, Object obj, Class<?> clazz, int deep, String parentFieldNames, String parentFieldDescs) throws Exception {
        if (deep > MAX_DEEP) {
            return;
        }
        deep++;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] fields = clazz.getDeclaredFields();
            loopField:
            for (Field field : fields) {
                // 包含CompareClass注解的对象
                if (!field.isAnnotationPresent(CompareClassConfig.class)) {
                    continue loopField;
                }
                // jdk11 无法访问 com.sun.beans.introspect.PropertyInfo
                //PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = getReadMethod(clazz, field);
                String fieldName = field.getName();
                if (getMethod == null) {
                    logger.info("找不到{}类的{}字段的get方法", clazz.getSimpleName(), fieldName);
                    continue loopField;
                }
                CompareClassConfig config = field.getAnnotation(CompareClassConfig.class);
                // 字段描述说明
                InnerType innerType = config.compareInner();
                // 字段值
                Object value = getMethod.invoke(obj);
                if (isNull(value)) {
                    continue loopField;
                }
                value = handlerDateValue(value, config);
                switch (innerType) {
                    // 不展开比较
                    case NONE:
                        result.add(new SameAnnotionField(deep, fieldName, config.value(), parentFieldNames, parentFieldDescs, value));
                        break;
                    // 比较单个对象 只比较'id', 记录'name'变化, 不在深入展开
                    case CASCADE_SINGLE:
                        Class<?> subClazz = value.getClass();
                        Method readMethod = getReadMethod(subClazz, config.innerIdentifyField());
                        Object name = readMethod.invoke(value);
                        result.add(new SameAnnotionField(deep, fieldName, config.value(), parentFieldNames, parentFieldDescs, name));
                        break;
                    // list对象 只循环 记录'name'变化 ,不再深入展开
                    case CASCADE_LIST:
                        Class<?> genericType = findListFieldGenericType(field);
                        if (genericType == null) {
                            break;
                        }
                        if (!(value instanceof List)) {
                            break;
                        }
                        List<?> listValue = (List<?>) value;
                        for (Object item : listValue) {
                            Method readMethod1 = getReadMethod(genericType, config.innerIdentifyField());
                            Object nameValue = readMethod1.invoke(item);
                            result.add(new SameAnnotionField(deep, fieldName, config.value(), parentFieldNames, parentFieldDescs, nameValue));
                        }
                        break;
                    // 单个对象, 需要展开对象内部进行比较
                    case SINGLE:
                        findByAnnotation(result, value, value.getClass(), deep, parentFieldNames + "," + field.getName(), parentFieldDescs + "," + config.value());
                        break;
                    // list对象, 需要展开对象内部进行比较
                    case LIST:
                        Class<?> genericType2 = findListFieldGenericType(field);
                        if (genericType2 == null) {
                            break;
                        }
                        if (!(value instanceof List)) {
                            break;
                        }
                        List<?> listValue2 = (List<?>) value;
                        findByAnnotation(result, listValue2, genericType2, deep, parentFieldNames + "," + field.getName(), parentFieldDescs + "," + config.value());
                        break;
                    default:
                        break;
                }
            }
        }

    }

    /**
     *  集合对象的： 根据注解获取属性， 用于统一新增 统一删除的属性记录
     * @param result
     * @param list
     * @param clazz 集合中的对象的泛型类型
     * @param deep
     * @param parentFieldNames
     * @param parentFieldDescs
     * @throws Exception
     */
    private static void findByAnnotation(List<SameAnnotionField> result, List<?> list, Class<?> clazz, int deep, String parentFieldNames, String parentFieldDescs) throws Exception {
        for (Object obj : list) {
            findByAnnotation(result, obj, clazz, deep, parentFieldNames, parentFieldDescs);
        }
    }

    /**
     * 处理单个对象属性的比较 只比较'id'，记录'name'
     *
     * @param result        存放结果集
     * @param main          主对象类型
     * @param field         当前属性
     * @param source        当前属性的原值
     * @param target 当前属性的新值
     * @param config        当前字段的注解
     * @throws Exception
     */
    private static void handlerCascadeSingle(List<CompareClassResultModel> result, Class<?> main, Field field,
                                             Object source, Object target, CompareClassConfig config) throws Exception {
        Class<?> clazz = field.getType();
        String identifyFlag = config.innerIdentifyField();
        String recordShowFlag = config.innerRecordShowField();
        if (StringUtils.isAnyBlank(identifyFlag, recordShowFlag)) {
            logger.info("{}未配置属性{} 对象比较的innerIdentifyField或比较记录属性innerRecordShowField", main.getSimpleName(),
                    config.value());
            return;
        }
        PropertyDescriptor idPd = new PropertyDescriptor(identifyFlag, clazz);
        Method idGetMethod = idPd.getReadMethod();
        if (idGetMethod == null) {
            logger.info("找不到{}类的{}字段的get方法", clazz.getSimpleName(), identifyFlag);
            return;
        }
        PropertyDescriptor namePd = new PropertyDescriptor(recordShowFlag, clazz);
        Method nameGetMethod = namePd.getReadMethod();
        if (nameGetMethod == null) {
            logger.info("找不到{}类的{}字段的get方法", clazz.getSimpleName(), recordShowFlag);
            return;
        }
        source = instanceIfNull(source, clazz);
        target = instanceIfNull(target, clazz);
        Object sourceId = idGetMethod.invoke(source);
        Object targetId = idGetMethod.invoke(target);
        Object sourceName = nameGetMethod.invoke(source);
        Object targetName = nameGetMethod.invoke(target);
        // 比较唯一标识的值以判断操作类型, 记录要展示的值
        CompareResultEnum resultEnum = compareValue(sourceId, targetId);
        if (CompareResultEnum.NONE != resultEnum) {
            result.add(
                    new CompareClassResultModel(field.getName(), config.value(), resultEnum, sourceName, targetName));
        }
    }

    /**
     * 处理单个属性值的比较
     *
     * @param result
     * @param sourceValue
     * @param targetValue
     * @param config
     * @param fieldName
     */
    private static void handlerNone(List<CompareClassResultModel> result, Object sourceValue, Object targetValue,
                                    CompareClassConfig config, String fieldName) {
        // 对日期值进行格式化处理
        sourceValue = handlerDateValue(sourceValue, config);
        targetValue = handlerDateValue(targetValue, config);
        CompareResultEnum resultEnum = compareValue(sourceValue, targetValue);
        if (CompareResultEnum.NONE != resultEnum) {
            result.add(new CompareClassResultModel(fieldName, config.value(), resultEnum, sourceValue, targetValue));
        }
    }

    /**
     * 对日期数据进行格式化处理
     *
     * @param value
     * @param config
     * @return
     */
    private static Object handlerDateValue(Object value, CompareClassConfig config) {
        if (value instanceof Date && StringUtils.isNoneBlank(config.datePattern())) {
            Date date = (Date) value;
            return DateFormatUtils.format(date, config.datePattern());
        }
        return value;
    }

    /**
     * list 对象转map<对象的某个属性(id), 对象的另外一个属性(name)或者是整个对象>
     *
     * @param list
     * @param clazz      :list中存储的对象的类型
     * @param keyField   key属性
     * @param valueField value属性 可以是空, 为空的时候,map的value为list中的对象
     */
    private static Map<String, Object> list2Map(List<?> list, Class<?> clazz, String keyField, String valueField)
            throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (Object item : list) {
            Object id = new PropertyDescriptor(keyField, clazz).getReadMethod().invoke(item);
            Object value = item;
            if (valueField != null) {
                value = new PropertyDescriptor(valueField, clazz).getReadMethod().invoke(item);
            }
            result.put(String.valueOf(id), value);
        }

        return result;
    }

    /**
     * <ul>
     * <li>添加： 空到非空</li>
     * <li>修改： 非空到非空</li>
     * <li>删除： 非空到空</li>
     * </ul>
     */
    private static CompareResultEnum compareValue(Object source, Object target) {
        if (isNull(source) && isNull(target)) {
            return CompareResultEnum.NONE;
        }
        if (isNull(source) && !isNull(target)) {
            return CompareResultEnum.ADD;
        }
        if (!isNull(source) && isNull(target)) {
            return CompareResultEnum.DELETE;
        }
        if (!source.equals(target)) {
            return CompareResultEnum.MODIFY;
        }
        return CompareResultEnum.NONE;
    }

    // 是否为空
    private static boolean isNull(Object obj) {
        return obj == null || StringUtils.isEmpty(String.valueOf(obj));
    }

    public static Method getReadMethod(Class<?> clazz, Field field) {
        return getReadMethod(clazz, field.getName());
    }
    public static Method getReadMethod(Class<?> clazz, String name) {
        String getter = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        try {
            return clazz.getMethod(getter);
        } catch (NoSuchMethodException e) {
           throw new RuntimeException(e);
        }
    }

    public static Method getWriteMethod(Class<?> clazz, Field field) {
        String name = field.getName();
        String setter = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        try {
            return clazz.getMethod(setter, field.getType());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /********************************** TEST ↓↓↓↓↓↓↓↓↓***************************************************************/
    public static void main(String[] args) throws Exception {

        testSub(false);
        testSub2(false);
        testExpand(false);
        testUnExpand(false);
        testRanom(true);
    }

    public static void testRanom(boolean test) throws Exception {
        if (!test) {
            return;
        }
        System.out.println("测试随机生成的数据");
        TypeTest source = TypeTest.init(4, 6);
        TypeTest target = TypeTest.init(3, 7);
        JsonUtil.printJson(source);
        JsonUtil.printJson(target);
        List<CompareClassResultModel> result = compare(TypeTest.class, source, target);
        result.forEach(System.out::println);
    }

    public static void testSub(boolean test) throws Exception {
        if (!test) {
            return;
        }
        System.out.println("测试不展开子属性 ");
        Sub s1 = new Sub(2);
        Sub s2 = new Sub(3);
        TypeTest source = new TypeTest();
        source.setSub(s1);
        TypeTest target = new TypeTest();
        target.setSub(s2);
        JsonUtil.printJson(s1);
        JsonUtil.printJson(s2);
        compare(TypeTest.class, source, target).forEach(System.out::println);
    }

    public static void testSub2(boolean test) throws Exception {
        if (!test) {
            return;
        }
        System.out.println("测试展开子属性");
        Sub s1 = new Sub(2);
        Sub s2 = new Sub(2);
        s2.setAge(21);
        ;
        TypeTest source = new TypeTest();
        source.setSub2(s1);
        TypeTest target = new TypeTest();
        target.setSub2(s2);
        JsonUtil.printJson(s1);
        JsonUtil.printJson(s2);
        compare(TypeTest.class, source, target).forEach(System.out::println);
    }

    public static void testUnExpand(boolean test) throws Exception {
        if (!test) {
            return;
        }
        System.out.println("测试不展开的list");
        List<Sub> expand1 = new ArrayList<Sub>();
        Sub s1 = new Sub(2);
        expand1.add(s1);
        List<Sub> expand2 = new ArrayList<Sub>();
        Sub s2 = new Sub(2);
        s2.setName("修改的名字");
        expand2.add(s2);
        TypeTest source = new TypeTest();
        source.setList(expand1);
        TypeTest target = new TypeTest();
        target.setList(expand2);
        JsonUtil.printJson(expand1);
        JsonUtil.printJson(expand2);
        compare(TypeTest.class, source, target).forEach(System.out::println);
    }

    public static void testExpand(boolean test) throws Exception {
        if (!test) {
            return;
        }
        System.out.println("测试展开list的子属性");
        List<Sub> expand1 = new ArrayList<Sub>();
        Sub s1 = new Sub(2);
        expand1.add(s1);
        List<Sub> expand2 = new ArrayList<Sub>();
        Sub s2 = new Sub(2);
        s2.setName("修改的名字");
        expand2.add(s2);
        TypeTest source = new TypeTest();
        source.setExpand(expand1);
        TypeTest target = new TypeTest();
        target.setExpand(expand2);
        JsonUtil.printJson(expand1);
        JsonUtil.printJson(expand2);
        compare(TypeTest.class, source, target).forEach(System.out::println);
    }

}

class TypeTest {
    public List<Sub> getList() {
        return list;
    }

    public void setList(List<Sub> list) {
        this.list = list;
    }

    public List<Sub> getExpand() {
        return expand;
    }

    public void setExpand(List<Sub> expand) {
        this.expand = expand;
    }

    public Sub getSub() {
        return sub;
    }

    public void setSub(Sub sub) {
        this.sub = sub;
    }

    public Sub getSub2() {
        return sub2;
    }

    public void setSub2(Sub sub2) {
        this.sub2 = sub2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @CompareClassConfig(value = "不展开列表项", compareInner = InnerType.CASCADE_LIST, innerIdentifyField = "id", innerRecordShowField = "name")
    List<Sub> list;

    @CompareClassConfig(value = "展开列表项", compareInner = InnerType.LIST)
    List<Sub> expand;

    @CompareClassConfig(value = "不展开的子对象", compareInner = InnerType.CASCADE_SINGLE)
    Sub sub;

    @CompareClassConfig(value = "展开的子对象", compareInner = InnerType.SINGLE)
    Sub sub2;

    Integer id;

    @CompareClassConfig(value = "age:")
    int age;

    @CompareClassConfig(value = "备注")
    String remark;

    @CompareClassConfig(value = "日期", datePattern = "yyyy-MM-dd HH:mm")
    Date date;

    public TypeTest() {
    }

    public static TypeTest init(int begin, int end) {
        TypeTest test = new TypeTest(begin, end);
        return test;
    }


    public TypeTest(int begin, int end) {
        int i = RandomUtils.nextInt(end, end);
        this.id = i;
        this.remark = "备注" + i;
        this.age = 50 + i;
        this.date = DateUtils.addDays(new Date(), i + 2);
        this.list = new ArrayList<Sub>();
        this.expand = new ArrayList<Sub>();
        this.sub = new Sub(begin, end);
        this.sub2 = new Sub(begin, end);
        for (; i > 0; i--) {
            list.add(new Sub(begin, end));
            expand.add(new Sub(begin, end));
        }

    }


}

class Sub {

    public List<Grandson> getGrandsonList() {
        return grandsonList;
    }

    public void setGrandsonList(List<Grandson> grandsonList) {
        this.grandsonList = grandsonList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Sub() {
    }

    public Sub(int i) {
        this.id = i + "";
        this.name = "张三" + i;
        this.age = 20 + i;
        this.grandsonList = new ArrayList<Grandson>();
        for (; i > 0; i--) {
            int j = RandomUtils.nextInt(2, 6);
            grandsonList.add(new Grandson(j));
        }

    }

    public Sub(int begin, int end) {
        int i = RandomUtils.nextInt(begin, end);
        this.id = i + "";
        this.name = "张三" + i;
        this.age = 20 + i;
        this.grandsonList = new ArrayList<Grandson>();
        for (; i > 0; i--) {
            int j = RandomUtils.nextInt(2, 6);
            grandsonList.add(new Grandson(j));
        }

    }

    @CompareClassConfig(value = "吾儿:", compareInner = InnerType.LIST)
    List<Grandson> grandsonList = new ArrayList<Grandson>();

    String id = "1";

    @CompareClassConfig(value = "儿名:")
    String name = "张三";

    @CompareClassConfig(value = "儿龄:")
    int age;

}

class Grandson {

    public Grandson() {
    }

    public Grandson(int i) {
        this.id = i + "";
        this.name = "孙子" + i;
        this.remark = "孙子备注" + i;
        this.time = DateUtils.addDays(new Date(), i);

    }

    private String id;

    @CompareClassConfig(value = "孙名:")
    private String name;
    ;

    @CompareClassConfig(value = "孙备:")
    private String remark;

    @CompareClassConfig(value = "时间:")
    private Date time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }



}
