--liquibase formatted sql

--changeset Fulai:202401241400
--comment: 回收站
CREATE TABLE t_sys_trash (
    id VARCHAR(32) not null primary key,
	http_method VARCHAR ( 7 ) comment '请求方式',
    request_url VARCHAR ( 256 ) comment '请求URL',
	thread_id VARCHAR ( 36 ) comment '线程ID',
    delete_by VARCHAR ( 36 ) comment '删除人',
    delete_time DATE NOT NULL comment '删除时间',
    table_name VARCHAR ( 36 ) NOT NULL comment '表名称',
    location VARCHAR(256) NOT NULL comment '删除语句位置',
    data text comment '数据'
) comment '数据回收站';