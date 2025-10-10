package com.kjlink.cloud.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用数据权限过滤插件
 * <p>
 * MBP官方的数据权限插件是全局开启，可能不符合有些项目需要，
 * 因此设计一个按需开启的权限过滤器
 * </p>
 * 使用方法：
 * <p>
 * 1.创建一个常量类存放sql片段，方便重用
 * <pre><code>
 *     public class PermissionSql {
 *         public static final String CREATE_BY = "t.create_by = {{USERNAME}}";
 *     }
 * </code></pre>
 * </p>
 * <p>
 * 2.声明一个DataPermissionSqlResolver，用于解析sql片段
 * <pre><code>
 *     //用框架自带的
 *     &#64;Bean
 *     public DataPermissionSqlResolver dataPermissionSqlResolver() {
 *         return new SimpleDataPermissionSqlResolver();
 *     }
 *     //需要扩展占位符号时，可以自定义一个实现类
 *     &#64;Component
 *     public class PermissionSql extends SimpleDataPermissionSqlResolver {
 *
 *     }
 * </code></pre>
 * </p>
 * <p>
 * 3.在mapper接口方法上增加注解
 * <pre><code>
 *     &#64;DataPermission(PermissionSql.CREATE_BY)
 *     List&lt;User&gt; findUserPage();
 * </code></pre>
 * </p>
 * <p>
 * 4.CrudMapper自带的方法不方便加注解，使用下面的方式临时设置权限拦截
 * <pre><code>
 *     //临时设置数据权限拦截，只能管一次select方法，然后就被清除了
 *     mapper.withPermission(PermissionSql.CREATE_BY).selectPage(...);
 *     //后续还要过滤的话，需要重新设置
 *     mapper.withPermission(PermissionSql.XXX).select(...);
 *     </code></pre>
 * </p>
 *
 * @author Fulai
 * @since 2025-07-09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataPermission {

    /**
     * 要过滤的表，可以不指定，默认sql分析结果的第一个表
     * @return
     */
    String table() default "";

    /**
     * sql条件过滤语句，建议配置为常量，重复使用
     * 统一约定使用t.表示当前表的别名
     * 可以使用{{XXX}}来引入常量
     * @return
     */
    String value();
}
