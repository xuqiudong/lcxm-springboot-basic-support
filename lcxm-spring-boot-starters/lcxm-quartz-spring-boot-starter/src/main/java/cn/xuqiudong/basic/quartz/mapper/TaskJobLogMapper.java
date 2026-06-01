package cn.xuqiudong.basic.quartz.mapper;

import cn.xuqiudong.basic.mybatisplus.mapper.StringCrudMapper;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务执行记录 Mapper
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Mapper
public interface TaskJobLogMapper extends StringCrudMapper<TaskJobLog> {

}
