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