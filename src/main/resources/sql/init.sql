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


