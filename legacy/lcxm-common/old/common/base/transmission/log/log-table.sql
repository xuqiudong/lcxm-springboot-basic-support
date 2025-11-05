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

