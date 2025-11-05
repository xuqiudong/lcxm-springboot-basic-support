package cn.xuqiudong.common.util.poi.excel.export.annotation;

import java.lang.annotation.*;

/**
 * 导出的列的注解
 * @author Vic.xu
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ExportField {

    /**
     * 标题
     */
    String title();

    /**
     * 父标题, 相同的父标题  用于合并表头 
     */
    String ptitle() default "";

    /**
     * 对齐方式：0-自动 1-左对齐 2-居中 3-右对齐
     */
    int align() default 0;

    /**
     * 一个字段在不同情况下可能需要导出或不需要，故把一个字段分别属于n个组，导出的时候选择其中一个组
     * eg. name 字段属于组{0,1,2},  则在导出 第0或1或2组的情况下name字段都会被导出
     */
    int[] groups() default {0};
    
}

