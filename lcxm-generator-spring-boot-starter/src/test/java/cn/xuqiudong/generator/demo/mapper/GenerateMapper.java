package cn.xuqiudong.generator.demo.mapper;

import cn.xuqiudong.common.base.mapper.StringCrudMapper;
import cn.xuqiudong.generator.demo.entity.Generate;
import org.apache.ibatis.annotations.Mapper;

/**
* 测试生成 Mapper
*
* @author Vic.xu
* @since 2025-11-10 15:53
*/
@Mapper
public interface GenerateMapper extends StringCrudMapper<Generate> {

}
