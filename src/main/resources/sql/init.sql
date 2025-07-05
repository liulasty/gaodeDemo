-- 用户表
CREATE TABLE t_sys_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);


-- 令牌表（用于实现令牌失效功能）
CREATE TABLE t_sys_token (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       token VARCHAR(255) NOT NULL UNIQUE,
                       token_type VARCHAR(50),
                       expired BOOLEAN NOT NULL,
                       revoked BOOLEAN NOT NULL,
                       user_id INT

);

CREATE TABLE t_amap_ip_geolocation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    STATUS VARCHAR(50),
    info VARCHAR(255),
    info_code VARCHAR(50),
    province VARCHAR(100),
    city VARCHAR(100),
    ad_code VARCHAR(50),
    rectangle VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 银行对账单表
CREATE TABLE t_bank_statement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    statement_number VARCHAR(50) NOT NULL,
    opening_balance DECIMAL(19,4) NOT NULL,
    closing_balance DECIMAL(19,4) NOT NULL
);

-- 交易记录表
CREATE TABLE t_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    statement_id BIGINT NOT NULL,
    transaction_date DATETIME NOT NULL,
    currency VARCHAR(10) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    business_summary VARCHAR(255),
    amount DECIMAL(19,4) NOT NULL,
    balance DECIMAL(19,4) NOT NULL,
    counterparty_name VARCHAR(100),
    counterparty_account VARCHAR(50)
);


CREATE TABLE t_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(50) NOT NULL COMMENT '权限名称，如READ_IMAGE',
    path VARCHAR(255) NOT NULL COMMENT '请求路径，如/images/**',
    method VARCHAR(10) COMMENT 'HTTP方法，如GET, POST等，NULL表示所有方法',
    description VARCHAR(255)

) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT '权限表，存储系统中的权限信息';

CREATE TABLE t_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(50) NOT NULL COMMENT '角色名称，如USER, ADMIN',
    description VARCHAR(255)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT '角色表，存储系统中所有角色的信息';

INSERT INTO t_role (NAME, description) VALUES
('USER', '普通用户角色，具有基本访问权限'),
('ADMIN', '管理员角色，具有所有管理权限'),
('GUEST', '游客角色，具有有限访问权限');

CREATE TABLE t_sys_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID，关联角色表',
    permission_id BIGINT NOT NULL COMMENT '权限ID，关联权限表'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='角色与权限关联表，定义角色与权限之间的关系';

CREATE TABLE t_sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID，关联用户表',
    role_id BIGINT NOT NULL COMMENT '角色ID，关联角色表',
    PRIMARY KEY (user_id, role_id)

) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表，定义用户与角色之间的关系';


SELECT id,NAME,path,method,description,created_at,updated_at FROM t_permission


CREATE TABLE `t_permission` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `name` VARCHAR(50) NOT NULL COMMENT '权限名称，如READ_IMAGE',
                                `path` VARCHAR(255) NOT NULL COMMENT '请求路径，如/images/**',
                                `method` VARCHAR(10) DEFAULT NULL COMMENT 'HTTP方法，如GET, POST等，NULL表示所有方法',
                                `component` VARCHAR(255) DEFAULT NULL COMMENT '前端组件路径',
                                `description` VARCHAR(255) DEFAULT NULL COMMENT '权限描述',
                                `icon` VARCHAR(50) DEFAULT NULL COMMENT '菜单图标',
                                `parent_id` BIGINT DEFAULT NULL COMMENT '父权限ID',
                                `order_num` INT DEFAULT '0' COMMENT '排序号',
                                `visible` TINYINT(1) DEFAULT '1' COMMENT '是否可见(0-隐藏,1-显示)',
                                `is_menu` TINYINT(1) DEFAULT '0' COMMENT '是否是菜单(0-否,1-是)',
                                `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_parent_id` (`parent_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';


-- 9. 插入初始权限数据 - 系统管理菜单
INSERT INTO `t_permission` (`name`, `path`, `method`, `component`, `description`, `icon`, `parent_id`, `order_num`, `visible`, `is_menu`) VALUES
-- 一级菜单
('系统管理', '/system', NULL, 'Layout', '系统管理', 'el-icon-s-tools', NULL, 1, 1, 1),

-- 二级菜单 - 用户管理
('用户管理', '/system/user', 'GET', '@/views/system/UserManagement.vue', '用户管理', 'el-icon-user', 1, 1, 1, 1),
('用户查询', '/api/users/**', 'GET', NULL, '用户查询权限', NULL, 2, 1, 1, 0),
('用户新增', '/api/users', 'POST', NULL, '用户新增权限', NULL, 2, 2, 1, 0),
('用户编辑', '/api/users/**', 'PUT', NULL, '用户编辑权限', NULL, 2, 3, 1, 0),
('用户删除', '/api/users/**', 'DELETE', NULL, '用户删除权限', NULL, 2, 4, 1, 0),

-- 二级菜单 - 角色管理
('角色管理', '/system/role', 'GET', '@/views/system/RoleManagement.vue', '角色管理', 'el-icon-s-custom', 1, 2, 1, 1),
('角色查询', '/api/roles/**', 'GET', NULL, '角色查询权限', NULL, 7, 1, 1, 0),
('角色新增', '/api/roles', 'POST', NULL, '角色新增权限', NULL, 7, 2, 1, 0),
('角色编辑', '/api/roles/**', 'PUT', NULL, '角色编辑权限', NULL, 7, 3, 1, 0),
('角色删除', '/api/roles/**', 'DELETE', NULL, '角色删除权限', NULL, 7, 4, 1, 0),

-- 二级菜单 - 权限管理
('权限管理', '/system/permission', 'GET', '@/views/system/PermissionManagement.vue', '权限管理', 'el-icon-lock', 1, 3, 1, 1),
('权限查询', '/api/permissions/**', 'GET', NULL, '权限查询权限', NULL, 12, 1, 1, 0),
('权限新增', '/api/permissions', 'POST', NULL, '权限新增权限', NULL, 12, 2, 1, 0),
('权限编辑', '/api/permissions/**', 'PUT', NULL, '权限编辑权限', NULL, 12, 3, 1, 0),
('权限删除', '/api/permissions/**', 'DELETE', NULL, '权限删除权限', NULL, 12, 4, 1, 0),

-- 一级菜单 - 内容管理
('内容管理', '/content', NULL, 'Layout', '内容管理', 'el-icon-document', NULL, 2, 1, 1),

-- 二级菜单 - 文章管理
('文章管理', '/content/article', 'GET', '@/views/content/ArticleManagement.vue', '文章管理', 'el-icon-notebook-2', 17, 1, 1, 1),
('文章查询', '/api/articles/**', 'GET', NULL, '文章查询权限', NULL, 18, 1, 1, 0),
('文章新增', '/api/articles', 'POST', NULL, '文章新增权限', NULL, 18, 2, 1, 0),
('文章编辑', '/api/articles/**', 'PUT', NULL, '文章编辑权限', NULL, 18, 3, 1, 0),
('文章删除', '/api/articles/**', 'DELETE', NULL, '文章删除权限', NULL, 18, 4, 1, 0),

-- 二级菜单 - 分类管理
('分类管理', '/content/category', 'GET', '@/views/content/CategoryManagement.vue', '分类管理', 'el-icon-collection', 17, 2, 1, 1),
('分类查询', '/api/categories/**', 'GET', NULL, '分类查询权限', NULL, 23, 1, 1, 0),
('分类新增', '/api/categories', 'POST', NULL, '分类新增权限', NULL, 23, 2, 1, 0),
('分类编辑', '/api/categories/**', 'PUT', NULL, '分类编辑权限', NULL, 23, 3, 1, 0),
('分类删除', '/api/categories/**', 'DELETE', NULL, '分类删除权限', NULL, 23, 4, 1, 0),

-- 一级菜单 - 数据统计
('数据统计', '/statistics', NULL, 'Layout', '数据统计', 'el-icon-data-line', NULL, 3, 1, 1),

-- 二级菜单 - 访问统计
('访问统计', '/statistics/visits', 'GET', '@/views/statistics/VisitsManagement.vue', '访问统计', 'el-icon-data-board', 28, 1, 1, 1),
('访问数据查询', '/api/statistics/visits/**', 'GET', NULL, '访问数据查询权限', NULL, 29, 1, 1, 0),

-- 二级菜单 - 用户分析
('用户分析', '/statistics/user-analysis', 'GET', '@/views/statistics/UserAnalysis.vue', '用户分析', 'el-icon-user-solid', 28, 2, 1, 1),
('用户分析查询', '/api/statistics/user/**', 'GET', NULL, '用户分析查询权限', NULL, 31, 1, 1, 0),

-- 一级菜单 - 育秧数据
('育秧数据', '/seedling', NULL, 'Layout', '育秧数据', 'el-icon-crop', NULL, 4, 1, 1),

-- 二级菜单 - 数据解析
('数据解析', '/seedling/analysis', 'GET', '@/views/seedling/SeedlingDataAnalysis.vue', '育秧数据解析', 'el-icon-coin', 33, 1, 1, 1),
('数据解析查询', '/api/seedling/**', 'GET', NULL, '育秧数据查询权限', NULL, 34, 1, 1, 0),
('数据上传', '/api/seedling/upload', 'POST', NULL, '育秧数据上传权限', NULL, 34, 2, 1, 0),

-- 一级菜单 - 图片采集
('图片采集', '/image', NULL, 'Layout', '图片采集', 'el-icon-picture', NULL, 5, 1, 1),

-- 二级菜单 - 图片采集
('图片采集', '/image/crawler', 'GET', '@/views/image/ImageCrawler.vue', '图片采集', 'el-icon-camera', 37, 1, 1, 1),
('图片采集操作', '/api/image/crawler/**', 'POST', NULL, '图片采集操作权限', NULL, 38, 1, 1, 0),

-- 二级菜单 - 图片列表采集
('图片列表采集', '/image/list-crawler', 'GET', '@/views/image/ImageListCrawler.vue', '图片列表采集', 'el-icon-picture-outline', 37, 2, 1, 1),
('图片列表采集操作', '/api/image/list-crawler/**', 'POST', NULL, '图片列表采集操作权限', NULL, 40, 1, 1, 0);



CREATE TABLE `sys_permission_rule` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `pattern` VARCHAR(255) NOT NULL COMMENT 'URL路径（Ant风格）',
                                       `http_method` VARCHAR(10) DEFAULT NULL COMMENT 'HTTP方法（GET/POST等）',
                                       `required_roles` VARCHAR(255) DEFAULT NULL COMMENT '所需角色，逗号分隔',
                                       `is_public` TINYINT(1) DEFAULT 0 COMMENT '是否公开访问（1=是）',
                                       `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
                                       PRIMARY KEY (`id`)
);


-- 清空现有数据（可选）
TRUNCATE TABLE sys_permission_rule;

-- 插入银行对账单相关权限规则
INSERT INTO sys_permission_rule (pattern, http_method, required_roles, is_public, enabled)
VALUES
    ('/api/bank-statements/upload', 'POST', 'ADMIN,ACCOUNTANT', 0, 1),
    ('/api/bank-statements/analyze', 'POST', 'ADMIN,ACCOUNTANT', 0, 1);

-- 插入认证相关公开接口
INSERT INTO sys_permission_rule (pattern, http_method, required_roles, is_public, enabled)
VALUES
    ('/api/auth/register', 'POST', NULL, 1, 1),
    ('/api/auth/login', 'POST', NULL, 1, 1),
    ('/api/auth/refresh-token', 'POST', NULL, 1, 1),
    ('/api/auth/check-email', 'GET', NULL, 1, 1);

-- 插入需要认证的接口
INSERT INTO sys_permission_rule (pattern, http_method, required_roles, is_public, enabled)
VALUES
    ('/api/auth/logout', 'POST', 'USER,ADMIN', 0, 1),
    ('/api/auth/user-info', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/auth/user-info2', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/auth/user-info3', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/auth/token', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/auth/role/*', 'GET', 'ADMIN', 0, 1),
    ('/api/auth/providers', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/auth/permissions', 'GET', 'ADMIN', 0, 1),
    ('/api/auth/oauth2-user', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/auth/check-admin', 'GET', 'ADMIN', 0, 1),
    ('/api/auth/admin-only', 'GET', 'ADMIN', 0, 1);

-- 插入地理编码相关接口
INSERT INTO sys_permission_rule (pattern, http_method, required_roles, is_public, enabled)
VALUES
    ('/api/geocode/reverse', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/geocode/ip', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/geocode/fullLocation', 'GET', 'USER,ADMIN', 0, 1),
    ('/api/geocode/address', 'GET', 'USER,ADMIN', 0, 1);



-- 允许公开访问Swagger文档
INSERT INTO `sys_permission_rule`
(`pattern`, `http_method`, `required_roles`, `is_public`, `enabled`)
VALUES
    ('/swagger-ui/**', 'GET', NULL, 1, 1),
    ('/v3/api-docs/**', 'GET', NULL, 1, 1),
    ('/doc.html', 'GET', NULL, 1, 1),
    ('/webjars/**', 'GET', NULL, 1, 1),
    ('/favicon.ico', 'GET', NULL, 1, 1);
