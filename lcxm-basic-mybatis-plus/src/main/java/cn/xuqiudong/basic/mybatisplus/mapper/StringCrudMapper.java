package cn.xuqiudong.basic.mybatisplus.mapper;

/**
 * 描述:
 *    String 类型的主键的Mapper， 基于mybatis-plus
 *    后期考虑使用String类型作为主键 （使用 IdType.ASSIGN_ID  雪花ID ）
 * @author Vic.xu
 * @since 2025-10-31 17:13
 */
public interface StringCrudMapper<T> extends MpGenericMapper<String, T>{
}
