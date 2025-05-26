-- 20250519 接收表新增表示字段
ALTER TABLE t_data_bridge_receive_message
    ADD COLUMN flag VARCHAR(64) COMMENT '标识';

-- 20250519  发送表新增表示字段
ALTER TABLE t_data_bridge_send_message
    ADD COLUMN flag VARCHAR(64) COMMENT '标识';

