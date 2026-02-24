package cn.xuqiudong.common.query;


import java.io.Serializable;

/**
 * 描述:
 *   查询对象的顶层接口，主要是作为一个标识：<br />
 *   用于构建查询对象QueryWrapper, 最终依赖对象字段上的QueryCondition注解
 * @see cn.xuqiudong.common.annotation.QueryCondition
 * @see cn.xuqiudong.common.builder.WrapperBuilder
 * @see cn.xuqiudong.common.util.QueryConditionUtils
 * @see cn.xuqiudong.common.base.mapper.MpGenericMapper
 * @author Vic.xu
 * @since 2026-01-08 14:14
 */
public interface MpQuery extends Serializable {

}
