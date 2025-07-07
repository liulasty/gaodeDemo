package com.example.demo.dto;

/**
 * 表示身份验证请求的记录类。
 * 包含用户的电子邮件和密码。
 * @author lz
 */
public record AuthenticationRequest(String user, String password) {

    /**
     * 构造函数，确保 user 和 password 不为 null。
     *
     * @param user    用户的电子邮件地址
     * @param password 用户的密码
     * @throws IllegalArgumentException 如果 user 或 password 为 null
     */
    public AuthenticationRequest {
        if (user == null || password == null) {
            throw new IllegalArgumentException("user and password must not be null");
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
        return "AuthenticationRequest[user=" + user + ", password=******]";
    }
}