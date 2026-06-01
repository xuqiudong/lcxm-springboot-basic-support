package cn.xuqiudong.basic.quartz.mapper;

import cn.xuqiudong.basic.mybatisplus.mapper.StringCrudMapper;
import cn.xuqiudong.basic.quartz.entity.TaskJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * quartz任务表 Mapper
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Mapper
public interface TaskJobMapper extends StringCrudMapper<TaskJob> {

}
