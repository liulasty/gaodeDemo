package com.example.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * JWT服务接口，处理JWT令牌的生成和验证
 */
public interface JwtService {
    
    /**
     * 为用户ID生成JWT令牌
     *
     * @param userId 用户ID
     * @param authorities 用户权限
     * @return JWT令牌
     */
    String generateToken(String userId, Collection<? extends GrantedAuthority> authorities);
    
    /**
     * 为认证对象生成JWT令牌
     *
     * @param authentication 认证对象
     * @return JWT令牌
     */
    String generateToken(Authentication authentication);
    
    /**
     * 为用户ID生成带有额外声明的JWT令牌
     *
     * @param userId 用户ID
     * @param authorities 用户权限
     * @param extraClaims 额外声明
     * @return JWT令牌
     */
    String generateToken(String userId, Collection<? extends GrantedAuthority> authorities, Map<String, Object> extraClaims);
    
    /**
     * 从JWT令牌中提取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    String extractUserId(String token);
    
    /**
     * 验证JWT令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    public Authentication getAuthentication(String token);
} 