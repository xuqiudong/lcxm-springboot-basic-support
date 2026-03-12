package cn.xuqiudong.basic.mybatisplus.query;


import cn.xuqiudong.basic.mybatisplus.annotation.QueryCondition;
import cn.xuqiudong.basic.mybatisplus.mapper.MpGenericMapper;
import cn.xuqiudong.basic.mybatisplus.builder.WrapperBuilder;
import cn.xuqiudong.basic.mybatisplus.util.QueryConditionUtils;

import java.io.Serializable;

/**
 * 描述:
 *   查询对象的顶层接口，主要是作为一个标识：<br />
 *   用于构建查询对象QueryWrapper, 最终依赖对象字段上的QueryCondition注解
 * @see QueryCondition
 * @see WrapperBuilder
 * @see QueryConditionUtils
 * @see MpGenericMapper
 * @author Vic.xu
 * @since 2026-01-08 14:14
 */
public interface MpQuery extends Serializable {

}
