/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 8.0.19 : Database - basic_support
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `t_data_bridge_receive_message` */

DROP TABLE IF EXISTS `t_data_bridge_receive_message`;

CREATE TABLE `t_data_bridge_receive_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `message_id` varchar(255) NOT NULL COMMENT '消息 ID（唯一标识）',
  `queue_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '队列名称',
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '模块名称',
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '动作名称',
  `message` text NOT NULL COMMENT '消息内容',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '待发送' COMMENT '状态（待处理、已处理、处理失败）',
  `retry_count` int DEFAULT '0' COMMENT '重试次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_enable` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `create_id` int DEFAULT NULL COMMENT '创建人id',
  `update_id` int DEFAULT NULL COMMENT '修改人id',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注说明',
  `flag` varchar(64) DEFAULT NULL COMMENT '标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_id` (`message_id`) COMMENT '消息 ID 唯一索引'
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='接收自mq的消息表';

/*Table structure for table `t_data_bridge_send_message` */

DROP TABLE IF EXISTS `t_data_bridge_send_message`;

CREATE TABLE `t_data_bridge_send_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `message_id` varchar(255) NOT NULL COMMENT '消息 ID（唯一标识）',
  `queue_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '队列名称',
  `module` varchar(255) NOT NULL COMMENT '模块名称',
  `action` varchar(255) NOT NULL COMMENT '动作名称',
  `message` text NOT NULL COMMENT '消息内容',
  `status` varchar(50) NOT NULL DEFAULT '待发送' COMMENT '状态（待发送、已发送、发送失败）',
  `retry_count` int DEFAULT '0' COMMENT '重试次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_enable` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `create_id` int DEFAULT NULL COMMENT '创建人id',
  `update_id` int DEFAULT NULL COMMENT '修改人id',
  `note` varchar(1024) DEFAULT NULL COMMENT '备注说明',
  `flag` varchar(64) DEFAULT NULL COMMENT '标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_id` (`message_id`) COMMENT '消息 ID 唯一索引'
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发送到mq的消息表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
