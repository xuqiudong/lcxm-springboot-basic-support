package cn.xuqiudong.common.base.transmission.log.model;

import java.util.Date;

/**
 * t_stl_s_third_log 第三方对接日志记录表
 * @author VIC.xu
 *
 */
public class ThirdLogModel {
	/*
	 CREATE TABLE `t_stl_s_third_log`
    (
        `id`             int(11) NOT NULL AUTO_INCREMENT,
        `third`          varchar(32)   DEFAULT NULL COMMENT '第三方标示',
        `request`        longtext COMMENT '请求数据',
        `response`       text COMMENT '返回数据',
        `create_date`    datetime      DEFAULT NULL COMMENT '创建时间',
        `status`         tinyint(1)    DEFAULT '1' COMMENT '状态：0- 失败 1-成功',
        `create_user_id` varchar(32)   DEFAULT NULL COMMENT '创建人id',
        `fid`            varchar(2048) DEFAULT NULL COMMENT '业务ids，如果有的话',
        PRIMARY KEY (`id`)
     ) ENGINE = InnoDB
   DEFAULT CHARSET = utf8 COMMENT ='第三方对接日志记录表'
	 */

    private Integer id;

    /**第三方标示*/
    private String third;

    /**请求数据*/
    private String request;

    /**返回数据*/
    private String response;

    private Date createDate;

    /**状态：0- 失败 1-成功*/
    private Integer status;

    private String createUserId;

    private String fid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

}
