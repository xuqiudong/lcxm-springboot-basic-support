-- 引入当前quartz starter的表的初始化脚本
-- 导出时间：2026-10-23
-- 用途：仅归档快照，不自动执行；部署增量变更单独维护
-- 使用前请先切换库：USE [database];
CREATE TABLE `t_task_job`
(
    `id`          varchar(32) NOT NULL COMMENT 'id',
    `name`        varchar(64)   DEFAULT NULL COMMENT '任务名',
    `task_code`   varchar(64)   DEFAULT NULL COMMENT '任务code',
    `task_group`  varchar(64)   DEFAULT NULL COMMENT '任务组',
    `cron`        varchar(128)  DEFAULT NULL COMMENT 'cron表达式',
    `status`      varchar(32)   DEFAULT NULL COMMENT '任务状态',
    `note`        varchar(1024) DEFAULT NULL COMMENT '备注',
    `create_time` datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `enabled`     tinyint       DEFAULT '1' COMMENT '是否启用',
    `deleted`     tinyint       DEFAULT '0' COMMENT '是否删除',
    `create_by`   varchar(32)   DEFAULT '-1' COMMENT '创建人id',
    `update_by`   varchar(32)   DEFAULT '-1' COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='quartz任务表';

CREATE TABLE `t_task_job_log`
(
    `id`            varchar(32) NOT NULL COMMENT 'id',
    `task_job_id`   varchar(32)   DEFAULT NULL COMMENT '任务id',
    `task_job_name` varchar(64)   DEFAULT NULL COMMENT '任务名称',
    `status`        varchar(32)   DEFAULT NULL COMMENT '任务状态',
    `cost_time`     bigint        DEFAULT '-1' COMMENT '任务用时(毫秒)',
    `result`        varchar(32)   DEFAULT NULL COMMENT '执行结果',
    `result_note`   varchar(2048) DEFAULT NULL COMMENT '结果描述',
    `create_time`   datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `enabled`       tinyint       DEFAULT '1' COMMENT '是否启用',
    `deleted`       tinyint       DEFAULT '0' COMMENT '是否删除',
    `create_by`     varchar(32)   DEFAULT '-1' COMMENT '创建人id',
    `update_by`     varchar(32)   DEFAULT '-1' COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务执行记录';

CREATE TABLE `t_task_job_log_detail`
(
    `id`              varchar(32) NOT NULL COMMENT 'id',
    `task_job_log_id` varchar(32)   DEFAULT NULL COMMENT '执行记录id',
    `result`          varchar(32)   DEFAULT NULL COMMENT '执行结果',
    `result_note`     varchar(2048) DEFAULT NULL COMMENT '结果描述',
    `create_time`     datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `enabled`         tinyint       DEFAULT '1' COMMENT '是否启用',
    `deleted`         tinyint       DEFAULT '0' COMMENT '是否删除',
    `create_by`       varchar(32)   DEFAULT '-1' COMMENT '创建人id',
    `update_by`       varchar(32)   DEFAULT '-1' COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务执行记录明细';
