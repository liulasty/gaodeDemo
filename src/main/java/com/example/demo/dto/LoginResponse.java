package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * JWT令牌
     */
    private String token;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 权限集合
     */
    private Collection<?> authorities;
} 