package cn.xuqiudong.basic.quartz.mapper;

import cn.xuqiudong.basic.mybatisplus.mapper.StringCrudMapper;
import cn.xuqiudong.basic.quartz.entity.TaskJobLogDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务执行记录明细 Mapper
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Mapper
public interface TaskJobLogDetailMapper extends StringCrudMapper<TaskJobLogDetail> {

    default int deleteBatchByJobLogIds(String[] jobIds) {
        if (jobIds == null || jobIds.length == 0) {
            return 0;
        }
        return delete(new LambdaQueryWrapper<TaskJobLogDetail>().in(TaskJobLogDetail::getTaskJobLogId, jobIds));
    }

}
