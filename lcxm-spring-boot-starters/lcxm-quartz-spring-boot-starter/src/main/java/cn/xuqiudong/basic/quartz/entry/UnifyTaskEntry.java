package cn.xuqiudong.basic.quartz.entry;

import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.job.AbstractTaskJob;
import cn.xuqiudong.basic.quartz.model.TaskJobHandlerModel;
import jakarta.annotation.PostConstruct;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 统一的任务入口 <br />
 * <p>
 * 所以，这里使用策略模式加枚举的方式，减少JobFactory的膨胀，
 * 在新增任务的时候，只要使用抽象类即可。
 * </p>
 *
 * @author Vic.xu
 * @since 2025-01-17 15:25
 */
public class UnifyTaskEntry {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifyTaskEntry.class);

    /**
     * 持有全部的任务处理类
     */
    private List<AbstractTaskJob> taskJobList;

    /**
     * 任务name(TaskJobFlag 的code) 对应任务处理函数
     */
    private Map<String, TaskJobHandlerModel<?>> allTaskJob;

    public UnifyTaskEntry(List<AbstractTaskJob> taskJobList) {
        this.taskJobList = taskJobList;
    }

    /**
     * 注册全部的定时任务函数
     */
    @PostConstruct
    private void registerTasks() {
        allTaskJob = new HashMap<>();
        if (CollectionUtils.isEmpty(taskJobList)) {
            LOGGER.info("当前环境没有需要统一入口的的任务处理函数");
            return;
        }
        //注册全部的定时任务函数
        for (AbstractTaskJob abstractTaskJob : taskJobList) {
            allTaskJob.putAll(abstractTaskJob.registerTasks(abstractTaskJob));
        }
        LOGGER.info("当前环境注册的统一任务处理函数个数为{}个", allTaskJob.size());
    }


    /**
     * 根据定时任务的标识执行相关定时任务
     *
     * @param taskName   任务的标识  TaskJob 中的 code
     * @param taskJobLog 用于保存日志  TaskJobLog
     * @return 任务结果
     * @throws Exception ex
     */
    @Transactional(rollbackFor = Exception.class)
    public String executeTask(String taskName, TaskJobLog taskJobLog) throws Exception {
        if (!existTask(taskName)) {
            return "没有[" + taskName + "]的处理函数!";
        }
        long start = System.currentTimeMillis();
        TaskJobHandlerModel<?> taskJobHandlerModel = allTaskJob.get(taskName);
        String result = taskJobHandlerModel.invoke(taskJobLog);
        long end = System.currentTimeMillis();
        LOGGER.info("执行[{}]任务用时[{}]ms", taskJobHandlerModel.getFlag().text(), (end - start));
        return result;

    }

    /**
     * 判断任务是否存在
     *
     * @param taskName 任务标识 code
     * @return true:存在
     */
    public boolean existTask(String taskName) {
        return MapUtils.isNotEmpty(allTaskJob) && allTaskJob.get(taskName) != null;
    }
}
