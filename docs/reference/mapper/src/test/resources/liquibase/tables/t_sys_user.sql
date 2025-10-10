--liquibase formatted sql

--changeset wanghy:t_sys_user-01
--comment: 创建用户表
create table t_sys_user (
  id VARCHAR(30) NOT NULL PRIMARY KEY COMMENT '主键',
  create_by VARCHAR(30) NOT NULL COMMENT '创建人',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_by VARCHAR(30) NOT NULL COMMENT '最后更新人',
  update_time datetime NOT NULL COMMENT '最后更新时间',
  user_name varchar(30) not null COMMENT '登录账号',
  full_name varchar(256) null comment '用户姓名',
  full_en_name varchar(256) null comment '用户英文姓名',
  gender varchar(2) COMMENT '性别',
  org_id BIGINT null comment '所属部门',
  legal_org_id BIGINT null comment '所属法人',
  email varchar(128) null comment 'email',
  mobile varchar(128) null comment '手机号码',
  telephone varchar(128) null comment '电话号码',
  is_locked varchar(2) COMMENT '是否锁定',
  is_organ_manager varchar(2) COMMENT '是否部门负责人',
  memo varchar(1024) null comment '用户描述',
  position varchar(4) null comment '职位',
  accredit_method varchar(4) null comment '授权方式',
  work_wei_xin varchar(32) null comment '企业微信号',
  language varchar(16) comment '语言ZH_CN中文，EN_US英文',
  employee_no varchar(50) null comment '员工工号'
) comment '用户';

--changeset fulai:t_user-02
--comment: 用户名不能为空并且唯一
create unique index uni_t_user_name on t_sys_user(user_name);