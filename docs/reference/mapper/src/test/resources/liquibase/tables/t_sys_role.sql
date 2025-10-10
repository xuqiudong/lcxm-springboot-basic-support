--liquibase formatted sql

--changeset wanghy:t_sys_role-01
--comment: 添加角色表
create table t_sys_role (
  id VARCHAR(30) NOT NULL PRIMARY KEY COMMENT '主键',
  create_by VARCHAR(30) NOT NULL COMMENT '创建人',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_by VARCHAR(30) NOT NULL COMMENT '最后更新人',
  update_time datetime NOT NULL COMMENT '最后更新时间',
  role_name varchar(128) null comment '角色名称',
  role_en_name varchar(512) null comment '角色英文名称',
  role_code varchar(128) null comment '角色代码',
  role_description varchar(256) null comment '角色描述',
  app varchar(10) null comment '角色所属，document（文档中心），asset（资产中心）'
) comment '角色表';
