package cn.xuqiudong.quartz.model;


import cn.xuqiudong.basic.core.model.BaseEntity;


/**
 * 定时任务执行记录明细 实体类
 *
 * @author Vic.xu
 */
public class TaskJobLogDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 执行记录id
     */
    private Integer taskJobLogId;

    /**
     * 执行结果
     */
    private String result;

    /**
     * 结果描述
     */
    private String resultNote;

    /**
     * 创建人id
     */
    private Integer createId;

    /**
     * 修改人id
     */
    private Integer updateId;


    /***************** set|get  start **************************************/
    /**
     * set：执行记录id
     */
    public TaskJobLogDetail setTaskJobLogId(Integer taskJobLogId) {
        this.taskJobLogId = taskJobLogId;
        return this;
    }

    /**
     * get：执行记录id
     */
    public Integer getTaskJobLogId() {
        return taskJobLogId;
    }

    /**
     * set：执行结果
     */
    public TaskJobLogDetail setResult(String result) {
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
    public TaskJobLogDetail setResultNote(String resultNote) {
        this.resultNote = resultNote;
        return this;
    }

    /**
     * get：结果描述
     */
    public String getResultNote() {
        return resultNote;
    }


    /***************** set|get  end **************************************/
}
