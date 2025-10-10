package cn.xuqiudong.common.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:标注一个字段非数据库字段
 * @author Vic.xu
 * @since 2024-03-04 14:48
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface NoneColumn {
}
