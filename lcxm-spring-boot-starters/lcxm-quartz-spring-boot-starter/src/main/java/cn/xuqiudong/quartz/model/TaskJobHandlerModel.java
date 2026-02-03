package cn.xuqiudong.quartz.model;

import cn.xuqiudong.quartz.annotation.TaskJobFlag;
import cn.xuqiudong.quartz.job.AbstractTaskJob;

import java.lang.reflect.Method;

/**
 * 描述:
 * 标注定时任务运行的实例和其对应的method
 *
 * @author Vic.xu
 * @since 2025-01-17 15:46
 */
public class TaskJobHandlerModel<T extends AbstractTaskJob> {

    private TaskJobFlag flag;

    private T jobInstance;

    /**
     * 被 TaskJobFlag 标注的方法  必须为 public
     */
    private Method method;

    public TaskJobHandlerModel(TaskJobFlag flag, T jobInstance, Method method) {
        this.flag = flag;
        this.jobInstance = jobInstance;
        this.method = method;
    }

    /**
     * 执行任务
     * 参数 TaskJobLog
     */
    public String invoke(TaskJobLog arg) throws Exception {
        return (String) method.invoke(jobInstance, arg);
    }

    public TaskJobFlag getFlag() {
        return flag;
    }

    public void setFlag(TaskJobFlag flag) {
        this.flag = flag;
    }

    public T getJobInstance() {
        return jobInstance;
    }

    public void setJobInstance(T jobInstance) {
        this.jobInstance = jobInstance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
