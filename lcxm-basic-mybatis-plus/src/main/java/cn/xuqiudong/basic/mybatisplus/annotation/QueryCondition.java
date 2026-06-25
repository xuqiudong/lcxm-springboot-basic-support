package cn.xuqiudong.basic.mybatisplus.annotation;

import cn.xuqiudong.basic.mybatisplus.enums.QueryOperation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 *    sql条件注解， 加在查询对象的字段上
 * @author Vic.xu
 * @since 2025-10-29 15:15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface QueryCondition {


    /**
     * 查询操作类型 必填
     */
    QueryOperation operation();

    /**
     * 当前字段对应的sql字段
     * 默认驼峰转下划线
     * 如果指定多个字段， 则 表示 or 关系
     */
    String[] columns() default {};

    /**
     * 是否忽略字段： 某些情况下字段加了注解后，想忽略
     */
    boolean ignore() default false;

    /**
     * 如果值为null 是否生成is null
     * 为 null 默认不查询
     */
    boolean allowNullQuery() default false;

    /**
     * 是否对字段进行trim操作
     */
    boolean trim() default true;

    /**
     * in 语句 时候，  分割字符串 的分隔符
     */
    String delimiter() default ",";


    /**
     * apply 操作的sql片段, 当 operation 为 APPLY 时， 必填项
     *  支持 {0} 单占位符，参数来自当前字段值
     *  EG: "date_format(create_time, '%Y-%m-%d') = {0}"
     *  EG: "FIND_IN_SET({0}, tags)"
     * @see QueryOperation#APPLY
     */
    String applySql() default "";

}
