package cn.xuqiudong.basic.framework.authentication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限注解：
 * <p>
 *    1.是否需要菜单权限：默认需要
 *    2.需要的权限标示  类似SHIRO的RequiresPermissions，若满足权限标示，则无需菜单权限
 *    3. 由于已经集成CAS  SSO   故此处不重复判断是否登陆
 * </p>
 * @author VIC.xu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Permission {

    /**是否需要菜单权限，默认不需要*/
    boolean url() default false;


    /**需要哪些权限标示， 如果配置了权限标示，则不再判断url*/
    String[] permissions();

    /**多个权限之间的关系: and / or*/
    Logical logical() default Logical.OR;

}
