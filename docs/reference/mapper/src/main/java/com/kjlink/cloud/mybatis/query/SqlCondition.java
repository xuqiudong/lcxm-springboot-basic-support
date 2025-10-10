package com.kjlink.cloud.mybatis.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b><code>sql条件的自定义注解</code></b>
 * <p/>
 * Description
 * <p/>
 * <b>Creation Time:</b>2023/1/11 19:55.
 *
 * @author zhangjc
 * @since system 1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface SqlCondition {

    /**
     * 默认字段名称驼峰转下划线，也可以手动指定
     *
     * @return 对应的数据库字段，可以多个，表示多个字段查同一个值，用or连接
     */
    String[] columns() default {};

    /**
     * @return SqlConditionEnum 对应的sql查询条件
     */
    SqlOperation op() default SqlOperation.AUTO;

    /**
     * 如果值为null，是否生成is null
     * 默认为null时不查询
     *
     * @return
     */
    boolean queryNull() default false;

    /**
     * 字符串是否trim成null,只针对于String类型的字段
     *
     * @return
     */
    boolean trim() default true;

    /**
     * 是否忽略该字段
     * 默认所有非transient字段都不忽略
     *
     * @return
     */
    boolean ignore() default false;

    /**
     * 字符串生成in语句时，默认按逗号分割
     *
     * @return
     */
    String splitter() default ",";
}
