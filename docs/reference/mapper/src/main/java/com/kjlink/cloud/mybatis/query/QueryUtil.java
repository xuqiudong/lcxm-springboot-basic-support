package com.kjlink.cloud.mybatis.query;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

/**
 * <h2>sql条件封装工具类</h2>
 * 此工具类主要是用来封装单表的查询条件的sql<br>
 * 主要是使用build(Object query, Class<T> entityClass)这个方法，query为自己建的查询vo类，（主要是包含前端需要的查询字段）<br>
 * entityClass为需要去查数据库的相关实体类<br>
 * vo类需要使用@SqlCondition这个注解，如果不写该注解，则按默认的来。<br>
 * <h2>默认规则</h2>
 * <p>
 * 1.如果该字段的值为空，则忽略这个字段;<br>
 * 2.如果该字段值不为空<br>
 * a.该字段的类型来定义查询条件的符号（String对应like，数组，集合对应IN,其他一致为equal），<br>
 * b.对应的数据库字段是根据字段名称驼峰标志转下划线生成，如字段名称userName,对应的数据库字段为user_name<br>
 * c.如果字段类型为String，则会默认去除字段前后空格，即trim();<br>
 *
 * <h3>举例：</h3>
 * 如userName的字段值为admin，则对应的wrapper为wrapper.like("user_name","admin"),对应的sql为
 * ...user_name like '%admin%'
 *
 * <h2>&#64;SqlCondition说明</h2>
 * 1.op(),即字段的比较条件，是like,还是in，还是大于等于，等等，详见SqlOperation枚举，其中AUTO意思 为按字段类型来定义详见默认规则中第2条的a：如果不写op(),
 * 默认AUTO;<br>
 * 2.columns(),为String类型数组，对应的数据库字段，可以为多个，主要是针对于这种业务情况：如客户名称在数据
 * 库中有中文，英文，简称3个字段，页面输入一个名称，需要查这三个字段的并集<br>
 * 例：
 * <pre><code>
 * &#64;SqlCondition(columns={"name","en_name,"short_name"}) private String name;<br>
 * SQL: and (name like %name% or en_name like %name% or short_name like %name%)
 * </code></pre>
 * 如果不写为默认规则中第2条的b规则来生成,默认{};<br>
 * 3.queryNull(),为true：如果字段值为空，增加查询条件 该字段 is null;false则不管为空，默认false<br>
 * 4.trim(),为true：字段类型为String时，去除前后空格；为false则不管空格；默认true；<br>
 * 5.splitter()，分割符号，字段类型为String时，op()为IN或NOT_IN,会将字段值split(splitter())成list，
 * 默认",";<br>
 * 6.ignore(),是否忽略，即字段值是否为空，都忽略该字段，默认false;<br>
 * <h3>举例</h3>
 * <pre><code>
 * &#64;SqlCondition(column="user_in",op = SqlOperation.IN,trim = true,queryNull = true, splitter = ";")
 * private String userIn;
 * </code></pre>
 * 其中column（字段名称驼峰标志转下划线），trim，queryNull可以忽略不写，trim，queryNull为false时不可忽略
 * PS：如果需要忽略某个字段不查询时，可按下面2个例子中任意一个、
 * <pre><code>
 * &#64;SqlCondition(ignore = true)
 * private String ignoreField;
 * private transient String transientFiled;
 * </code></pre>
 * <b>Creation Time:</b>2023/1/11 18:53.
 *
 * @author zhangjc
 * @since system 1
 */
public class QueryUtil {

    private static final String DEFAULT_SPLIT = ",";
    /**
     * 定义的一些基本类型的包装类的name,用来后续用来判断相关class的类型
     */
    /*public final static String[] classTypes = {"java.lang.Integer","java.lang.Double",
            "java.lang.Float","java.lang.Long","java.lang.Short","java.lang.Byte",
            "java.lang.Boolean","java.lang.Character"};*/
    private static Map<Class<?>, List<PropInfo>> cacheMap = new ConcurrentHashMap<>();

    /**
     * @deprecated 使用Where.create(queryObj)代替，或Wheres.create(queryObj)
     */
    @Deprecated
    public static Where build(Object query) {
        Where where = Where.create();
        buildInternal(where, query);
        return where;
    }

    static <T> void buildInternal(WhereBuilder where, Object query) {
        Class<?> queryClass = query.getClass();
        List<PropInfo> propInfos = cacheMap.computeIfAbsent(queryClass, (x) -> reflectProperties(queryClass));

        for (PropInfo propInfo : propInfos) {
            Object value = propInfo.getValue(query);
            boolean stringFlag = String.class == propInfo.getFieldType();
            boolean collFlag = Collection.class.isAssignableFrom(propInfo.getFieldType());
            //如果字段值为空,String需判断是否是空字符串，数组集合需判断size是否为0
            if (null == value || (stringFlag && StrUtil.isBlank((String) value)) ||
                    (collFlag && CollectionUtil.isEmpty((Collection<?>) value)) || ArrayUtil.isEmpty(value)) {
                if (propInfo.isQueryNull()) {
                    //wrapper.isNull(propInfo.getColumns()[0]);
                    where.and(propInfo.getColumns()[0], SqlOperation.IS_NULL, null);
                }
            } else {
                if (stringFlag) {
                    if (propInfo.isTrim()) {
                        value = StrUtil.trim((String) value);
                    }
                    if (propInfo.getOp() == SqlOperation.IN || propInfo.getOp() == SqlOperation.NOT_IN) {
                        value = StrUtil.split((String) value, propInfo.getSplitter());
                    }
                }
                //由于wrapper的本身限制，用in或notin时，不能用数组，只能用集合,所以如果是集合，需要转化成集合
                if ((propInfo.getOp() == SqlOperation.IN || propInfo.getOp() == SqlOperation.NOT_IN) &&
                        propInfo.getFieldType().isArray()) {
                    value = Convert.toList(value);
                }
                assembleWrapper(where, propInfo, value);
            }
        }
    }

    private static List<PropInfo> reflectProperties(Class<?> queryClass) {
        List<PropInfo> propList = new ArrayList<>();
        //获取字段
        BeanDesc beanDesc = BeanUtil.getBeanDesc(queryClass);
        Collection<PropDesc> props = beanDesc.getProps();
        for (PropDesc prop : props) {
            Field field = prop.getField();
            SqlCondition sca = field.getAnnotation(SqlCondition.class);
            //过滤掉原生的Transient字段以及static修饰的字段
            boolean isTransient = Modifier.isTransient(field.getModifiers());
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            //忽略属性
            if (isTransient || isStatic || (null != sca && sca.ignore())) {
                continue;
            }

            PropInfo propInfo = new PropInfo(prop);
            //设置fieldInfo的FieldType字段值
            propInfo.setFieldType(field.getType());
            if (sca != null) {
                SqlOperation op = sca.op();
                if (op == SqlOperation.AUTO) {
                    op = getOp(field.getType());
                }
                String[] columns = sca.columns();
                if (columns == null || columns.length == 0) {
                    columns = new String[]{StrUtil.toUnderlineCase(field.getName())};
                }
                propInfo.setColumns(columns);
                propInfo.setOp(op);
                propInfo.setQueryNull(sca.queryNull());
                propInfo.setTrim(sca.trim());
                propInfo.setSplitter(sca.splitter());
            } else {
                String[] columns = new String[]{StrUtil.toUnderlineCase(field.getName())};
                propInfo.setColumns(columns);
                SqlOperation op = getOp(field.getType());
                propInfo.setOp(op);
                propInfo.setQueryNull(false);
                propInfo.setTrim(false);
                propInfo.setSplitter(DEFAULT_SPLIT);
            }
            propList.add(propInfo);
        }
        return propList;
    }

    private static SqlOperation getOp(Class<?> typeClass) {
          /*if(typeClass.isEnum()||typeClass.isPrimitive()|| ArrayUtil.contains(classTypes,typeClass.getName())){
          }else*/
        if (Collection.class.isAssignableFrom(typeClass) || typeClass.isArray()) {
            return SqlOperation.IN;
        } else if (typeClass == String.class) {
            return SqlOperation.LIKE;
        }
        return SqlOperation.EQ;
    }

    private static <T> void assembleWrapper(WhereBuilder where, PropInfo propInfo, Object value) {
        SqlOperation op = propInfo.getOp();
        String[] columns = propInfo.getColumns();
        if (columns.length > 1) {
            where.andOrs(columns, op, value);
        } else {
            where.and(columns[0], op, value);
        }
    }
}