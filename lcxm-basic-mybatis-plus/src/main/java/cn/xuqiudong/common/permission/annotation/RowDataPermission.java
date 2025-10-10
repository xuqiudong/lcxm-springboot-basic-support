package cn.xuqiudong.common.permission.annotation;


import cn.xuqiudong.common.permission.RowDataHelper;
import cn.xuqiudong.common.permission.enums.JointLogic;
import cn.xuqiudong.common.permission.enums.RowDataHandlerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 *    标记方法需要启用数据权限（固定组合场景）
 * @see RowDataHelper#start(RowDataHandlerType, String)
 * @author Vic.xu
 * @since 2025-09-23 15:41
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RowDataPermission {


    /**
     * 数据权限规则列表
     */
    Item[] value();

    /**
     * 单个数据权限规则（字段 + 权限类型）
     */
    @Target({}) // 仅作为注解参数使用
    @Retention(RetentionPolicy.RUNTIME)
    @interface Item {
        /**
         * 数据库字段名（带表别名，如"a.dept_id"）
         */
        String column();

        /**
         * 权限类型（枚举类，必须实现RowDataHandlerType接口）
         */
        Class<? extends Enum<? extends RowDataHandlerType>> type();

        /**
         * 枚举中的具体权限值（如USER、MANAGE_ORG）
         * 因为 注解中不能写接口, 这里是为了保持  #type 的扩展性
         */
        String value();

        /**
         * 前置判断条件（如"a.xx_id is not null"） 最终形成 WHERE条件为：(前置条件 AND 权限条件)
         */
        String precondition() default "";

        /**
         * 前置判断条件 和权限sql 逻辑关系（默认为OR）
         */
        JointLogic jointLogic() default JointLogic.OR;
    }
}
