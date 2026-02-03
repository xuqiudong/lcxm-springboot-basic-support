package cn.xuqiudong.quartz.model;


import cn.xuqiudong.common.base.model.BaseEntity;


/**
 * 定时任务执行记录 实体类
 *
 * @author Vic.xu
 */
public class TaskJobLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    private Integer taskJobId;

    /**
     * 任务名称
     */
    private String taskJobName;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 执行结果
     */
    private String result;

    /**
     * 结果描述
     */
    private String resultNote;

    /**
     * 执行人
     */
    private String executor;


    /***************** set|get  start **************************************/
    /**
     * set：任务id
     */
    public TaskJobLog setTaskJobId(Integer taskJobId) {
        this.taskJobId = taskJobId;
        return this;
    }

    /**
     * get：任务id
     */
    public Integer getTaskJobId() {
        return taskJobId;
    }

    /**
     * set：任务名称
     */
    public TaskJobLog setTaskJobName(String taskJobName) {
        this.taskJobName = taskJobName;
        return this;
    }

    /**
     * get：任务名称
     */
    public String getTaskJobName() {
        return taskJobName;
    }

    /**
     * set：任务状态
     */
    public TaskJobLog setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * get：任务状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * set：执行结果
     */
    public TaskJobLog setResult(String result) {
        this.result = result;
        return this;
    }

    /**
     * get：执行结果
     */
    public String getResult() {
        return result;
    }

    /**
     * set：结果描述
     */
    public TaskJobLog setResultNote(String resultNote) {
        this.resultNote = resultNote;
        return this;
    }

    /**
     * get：结果描述
     */
    public String getResultNote() {
        return resultNote;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    /***************** set|get  end **************************************/
}
