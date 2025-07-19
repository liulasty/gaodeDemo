/*
SQLyog Professional v12.09 (64 bit)
MySQL - 8.0.40 : Database - dynamic_menu
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`dynamic_menu` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `dynamic_menu`;

/*Table structure for table `oauth2_authorization` */

DROP TABLE IF EXISTS `oauth2_authorization`;

CREATE TABLE `oauth2_authorization` (
                                        `id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `registered_client_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `principal_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `authorization_grant_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `authorized_scopes` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `attributes` blob,
                                        `state` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `authorization_code_value` blob,
                                        `authorization_code_issued_at` timestamp NULL DEFAULT NULL,
                                        `authorization_code_expires_at` timestamp NULL DEFAULT NULL,
                                        `authorization_code_metadata` blob,
                                        `access_token_value` blob,
                                        `access_token_issued_at` timestamp NULL DEFAULT NULL,
                                        `access_token_expires_at` timestamp NULL DEFAULT NULL,
                                        `access_token_metadata` blob,
                                        `access_token_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `access_token_scopes` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `refresh_token_value` blob,
                                        `refresh_token_issued_at` timestamp NULL DEFAULT NULL,
                                        `refresh_token_expires_at` timestamp NULL DEFAULT NULL,
                                        `refresh_token_metadata` blob,
                                        `oidc_id_token_value` blob,
                                        `oidc_id_token_issued_at` timestamp NULL DEFAULT NULL,
                                        `oidc_id_token_expires_at` timestamp NULL DEFAULT NULL,
                                        `oidc_id_token_metadata` blob,
                                        `oidc_id_token_claims` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                        `user_code_value` blob,
                                        `user_code_issued_at` timestamp NULL DEFAULT NULL,
                                        `user_code_expires_at` timestamp NULL DEFAULT NULL,
                                        `user_code_metadata` blob,
                                        `device_code_value` blob,
                                        `device_code_issued_at` timestamp NULL DEFAULT NULL,
                                        `device_code_expires_at` timestamp NULL DEFAULT NULL,
                                        `device_code_metadata` blob,
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `oauth2_authorization_consent` */

DROP TABLE IF EXISTS `oauth2_authorization_consent`;

CREATE TABLE `oauth2_authorization_consent` (
                                                `registered_client_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                                                `principal_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
                                                `authorities` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
                                                PRIMARY KEY (`registered_client_id`,`principal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `oauth2_registered_client` */

DROP TABLE IF EXISTS `oauth2_registered_client`;

CREATE TABLE `oauth2_registered_client` (
                                            `id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `client_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `client_id_issued_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            `client_secret` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `client_secret_expires_at` timestamp NULL DEFAULT NULL,
                                            `client_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `client_authentication_methods` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `authorization_grant_types` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `redirect_uris` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `post_logout_redirect_uris` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                            `scopes` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `client_settings` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            `token_settings` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL,
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `oauth2_user_binding` */

DROP TABLE IF EXISTS `oauth2_user_binding`;

CREATE TABLE `oauth2_user_binding` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `user_id` bigint NOT NULL COMMENT '本地用户ID',
                                       `provider` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提供商(github, google等)',
                                       `provider_user_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提供商用户ID',
                                       `provider_username` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提供商用户名',
                                       `provider_email` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '提供商邮箱',
                                       `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                       `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_provider_user_id` (`provider`,`provider_user_id`),
                                       KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2用户绑定表';

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `component` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `parent_id` bigint DEFAULT NULL,
                            `sort` int DEFAULT '0',
                            `hidden` tinyint(1) DEFAULT '0',
                            `permission` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            KEY `parent_id` (`parent_id`),
                            CONSTRAINT `sys_menu_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `sys_menu` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `sys_permission_rule` */

DROP TABLE IF EXISTS `sys_permission_rule`;

CREATE TABLE `sys_permission_rule` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `pattern` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'URL路径（Ant风格）',
                                       `http_method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'HTTP方法（GET/POST等）',
                                       `required_roles` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所需角色，逗号分隔',
                                       `is_public` tinyint(1) DEFAULT '0' COMMENT '是否公开访问（1=是）',
                                       `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                            `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                            `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint NOT NULL,
                                 `menu_id` bigint NOT NULL,
                                 PRIMARY KEY (`role_id`,`menu_id`),
                                 KEY `menu_id` (`menu_id`),
                                 CONSTRAINT `sys_role_menu_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
                                 CONSTRAINT `sys_role_menu_ibfk_2` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
                                 `user_id` bigint NOT NULL,
                                 `role_id` bigint NOT NULL,
                                 PRIMARY KEY (`user_id`,`role_id`),
                                 KEY `role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `t_bank_statement` */

DROP TABLE IF EXISTS `t_bank_statement`;

CREATE TABLE `t_bank_statement` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `customer_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `account_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `currency` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `start_date` date NOT NULL,
                                    `end_date` date NOT NULL,
                                    `statement_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                    `opening_balance` decimal(19,4) NOT NULL,
                                    `closing_balance` decimal(19,4) NOT NULL,
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `t_data_timeout_record` */

DROP TABLE IF EXISTS `t_data_timeout_record`;

CREATE TABLE `t_data_timeout_record` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `group_id` varchar(255) NOT NULL COMMENT '消息组ID（由设备ID和消息ID组成）',
                                         `device_id` varchar(255) DEFAULT NULL COMMENT '设备ID',
                                         `message_id` varchar(255) DEFAULT NULL COMMENT '消息ID',
                                         `expire_time` bigint NOT NULL COMMENT '超时时间戳',
                                         `cleanup_time` bigint NOT NULL COMMENT '清理时间戳',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=427 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `t_device_data_record` */

DROP TABLE IF EXISTS `t_device_data_record`;

CREATE TABLE `t_device_data_record` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `message_id` varchar(255) DEFAULT NULL COMMENT '消息ID',
                                        `device_id` varchar(255) DEFAULT NULL COMMENT '设备ID',
                                        `data_type` varchar(255) DEFAULT NULL COMMENT '数据类型',
                                        `assembled` varchar(255) DEFAULT NULL COMMENT '组装数据',
                                        `timestamp` bigint NOT NULL COMMENT '时间戳',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1548 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `t_image_src_crawl_record` */

DROP TABLE IF EXISTS `t_image_src_crawl_record`;

CREATE TABLE `t_image_src_crawl_record` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                            `title` varchar(255) DEFAULT NULL COMMENT '标题',
                                            `image_sum` bigint DEFAULT NULL COMMENT '获取到图片',
                                            `href` varchar(512) DEFAULT NULL COMMENT '链接地址',
                                            `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            PRIMARY KEY (`id`),
                                            KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片源爬取记录表';

/*Table structure for table `t_image_src_url` */

DROP TABLE IF EXISTS `t_image_src_url`;

CREATE TABLE `t_image_src_url` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `alt` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片的alt描述',
                                   `href` varchar(1023) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片链接地址',
                                   `page` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '页面标识',
                                   `src` varchar(1023) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片源地址',
                                   `timestamp` bigint NOT NULL COMMENT '时间戳',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1801 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片目录源链接';

/*Table structure for table `t_image_src_url_detail` */

DROP TABLE IF EXISTS `t_image_src_url_detail`;

CREATE TABLE `t_image_src_url_detail` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `title` varchar(255) DEFAULT NULL COMMENT '标题',
                                          `tid` bigint DEFAULT NULL COMMENT '解析记录ID',
                                          `index` bigint DEFAULT NULL COMMENT '索引',
                                          `src` varchar(1024) DEFAULT NULL COMMENT '图片源URL',
                                          `alt` varchar(512) DEFAULT NULL COMMENT '替代文本',
                                          `attributes` text COMMENT '其他属性',
                                          `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_created_time` (`created_time`),
                                          KEY `idx_updated_time` (`updated_time`)
) ENGINE=InnoDB AUTO_INCREMENT=531 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片源URL详情表';

/*Table structure for table `t_message` */

DROP TABLE IF EXISTS `t_message`;

CREATE TABLE `t_message` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `message_id` varchar(255) DEFAULT NULL COMMENT '消息ID',
                             `device_id` varchar(255) DEFAULT NULL COMMENT '设备ID',
                             `data_type` varchar(255) DEFAULT NULL COMMENT '数据类型',
                             `total_fragments` int DEFAULT NULL COMMENT '总分片数',
                             `fragment_index` int DEFAULT NULL COMMENT '当前分片索引',
                             `payload` varchar(1000) DEFAULT NULL COMMENT '有效载荷',
                             `timestamp` bigint DEFAULT NULL COMMENT '时间戳',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6171 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `t_permission` */

DROP TABLE IF EXISTS `t_permission`;

CREATE TABLE `t_permission` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `name` varchar(50) NOT NULL COMMENT '权限名称，如READ_IMAGE',
                                `path` varchar(255) NOT NULL COMMENT '请求路径，如/images/**',
                                `method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法，如GET, POST等，NULL表示所有方法',
                                `component` varchar(255) DEFAULT NULL COMMENT '前端组件路径',
                                `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
                                `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
                                `parent_id` bigint DEFAULT NULL COMMENT '父权限ID',
                                `order_num` int DEFAULT '0' COMMENT '排序号',
                                `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见(0-隐藏,1-显示)',
                                `is_menu` tinyint(1) DEFAULT '0' COMMENT '是否是菜单(0-否,1-是)',
                                `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';

/*Table structure for table `t_role` */

DROP TABLE IF EXISTS `t_role`;

CREATE TABLE `t_role` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `NAME` varchar(50) NOT NULL COMMENT '角色名称，如USER, ADMIN',
                          `description` varchar(255) DEFAULT NULL,
                          `created_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                          `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表，存储系统中所有角色的信息';

/*Table structure for table `t_sys_role_permission` */

DROP TABLE IF EXISTS `t_sys_role_permission`;

CREATE TABLE `t_sys_role_permission` (
                                         `role_id` bigint NOT NULL COMMENT '角色ID，关联角色表',
                                         `permission_id` bigint NOT NULL COMMENT '权限ID，关联权限表'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色与权限关联表，定义角色与权限之间的关系';

/*Table structure for table `t_sys_user` */

DROP TABLE IF EXISTS `t_sys_user`;

CREATE TABLE `t_sys_user` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                              `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                              `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              `status` int DEFAULT NULL,
                              `enabled` tinyint(1) DEFAULT '1',
                              `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Table structure for table `t_sys_user_role` */

DROP TABLE IF EXISTS `t_sys_user_role`;

CREATE TABLE `t_sys_user_role` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `user_id` bigint NOT NULL COMMENT '用户ID，关联用户表',
                                   `role_id` bigint NOT NULL COMMENT '角色ID，关联角色表',
                                   `created_at` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`,`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与角色关联表，定义用户与角色之间的关系';

/*Table structure for table `t_transaction` */

DROP TABLE IF EXISTS `t_transaction`;

CREATE TABLE `t_transaction` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `statement_id` bigint NOT NULL,
                                 `transaction_date` datetime NOT NULL,
                                 `currency` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `transaction_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `business_summary` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `amount` decimal(19,4) NOT NULL,
                                 `balance` decimal(19,4) NOT NULL,
                                 `counterparty_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 `counterparty_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                 PRIMARY KEY (`id`,`currency`)
) ENGINE=InnoDB AUTO_INCREMENT=247 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
