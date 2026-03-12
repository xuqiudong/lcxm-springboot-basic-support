package cn.xuqiudong.basic.core.aspect.annotation;

import java.lang.annotation.*;

/**
 * 描述: 表单重复提交验证
 * @author Vic.xu
 * @since 2022-03-16 10:28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface RepeatCommitVersion {

    /**
     * table name
     */
    String table();

    /**
     * version  column in table
     */
    String column() default "version";

    /**
     * version name in form input  <input name="version" />
     */
    String versionAttribute() default "version";

    /**
     * id name in  form
     */
    String idAttribute() default "id";
}
