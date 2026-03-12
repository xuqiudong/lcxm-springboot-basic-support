package cn.xuqiudong.quartz.model;


import cn.xuqiudong.basic.core.model.BaseEntity;
import cn.xuqiudong.quartz.enums.QuartzStatusEnum;

/**
 * quartz任务表 实体类
 *
 * @author Vic.xu
 */
public class TaskJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名
     */
    private String name;

    /**
     * 任务code
     */
    private String code;

    /**
     * 任务组
     */
    private String group;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 任务状态
     */
    private QuartzStatusEnum status;

    /**
     * 备注
     */
    private String note;

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
     * set：任务名
     */
    public TaskJob setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * get：任务名
     */
    public String getName() {
        return name;
    }

    /**
     * set：任务code
     */
    public TaskJob setCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * get：任务code
     */
    public String getCode() {
        return code;
    }

    /**
     * set：任务组
     */
    public TaskJob setGroup(String group) {
        this.group = group;
        return this;
    }

    /**
     * get：任务组
     */
    public String getGroup() {
        return group;
    }

    /**
     * set：cron表达式
     */
    public TaskJob setCron(String cron) {
        this.cron = cron;
        return this;
    }

    /**
     * get：cron表达式
     */
    public String getCron() {
        return cron;
    }

    /**
     * set：任务状态
     */
    public TaskJob setStatus(QuartzStatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * get：任务状态
     */
    public QuartzStatusEnum getStatus() {
        return status;
    }

    /**
     * set：备注
     */
    public TaskJob setNote(String note) {
        this.note = note;
        return this;
    }

    /**
     * get：备注
     */
    public String getNote() {
        return note;
    }

    public String getStatusText() {
        return status == null ? "" : status.getText();
    }

    /***************** set|get  end **************************************/
}
