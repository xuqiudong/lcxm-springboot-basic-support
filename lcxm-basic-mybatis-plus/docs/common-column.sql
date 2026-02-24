# 给表追加通用字段: 创建时间、修改时间、是否启用、是否删除、创建人id、修改人id字段
ALTER TABLE table_name
    ADD (
        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT "修改时间",
        `enabled` TINYINT DEFAULT '1' COMMENT '是否启用',
        `deleted` TINYINT DEFAULT '0' COMMENT '是否删除',
        `create_by` varchar(32) DEFAULT '-1' COMMENT '创建人id',
        `update_by` varchar(32) DEFAULT '-1' COMMENT '修改人id'
        );