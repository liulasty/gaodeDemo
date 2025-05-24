package com.example.demo.dto;

import java.util.Objects;

/**
 * 表示身份验证请求的记录类。
 * 包含用户的电子邮件和密码。
 * @author lz
 */
public record AuthenticationRequest(String email, String password) {

    /**
     * 构造函数，确保 email 和 password 不为 null。
     *
     * @param email    用户的电子邮件地址
     * @param password 用户的密码
     * @throws IllegalArgumentException 如果 email 或 password 为 null
     */
    public AuthenticationRequest {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }
    }

    /**
     * 生成刷新令牌。
     * 当前方法未实现，调用时将抛出 UnsupportedOperationException。
     *
     * @return 刷新令牌
     * @throws UnsupportedOperationException 如果方法未实现
     */
    public String refreshToken() {
        throw new UnsupportedOperationException("Refresh token generation is not implemented");
    }

    /**
     * 重写 toString 方法，隐藏密码字段以保护敏感信息。
     *
     * @return 不包含密码的字符串表示形式
     */
    @Override
    public String toString() {
        return "AuthenticationRequest[email=" + email + ", password=******]";
    }
}