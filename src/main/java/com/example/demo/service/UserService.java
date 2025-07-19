package com.example.demo.service;

import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RegisterResponse;
import com.example.demo.entity.User;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 注册新用户
     *
     * @param request 注册请求
     * @return 注册结果响应
     */
    RegisterResponse register(RegisterRequest request);
    
    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @return true表示存在，false表示不存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Integer id);
    
    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    User findByEmail(String email);

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);
    
    /**
     * 用户登录认证
     *
     * @param request 认证请求对象，包含用户邮箱和密码
     * @return 认证响应对象，包含JWT令牌
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);
    
    /**
     * 刷新令牌
     *
     * @param request 请求对象，包含邮箱和刷新令牌
     * @return 认证响应对象，包含新的JWT令牌
     */
    AuthenticationResponse refreshToken(AuthenticationRequest request);
    
    /**
     * 用户注销
     *
     * @param request 认证请求对象，包含用户邮箱和刷新令牌
     */
    void logout(HttpServletRequest request);
    
    /**
     * 验证邮箱格式是否有效
     * 
     * @param email 待验证的邮箱地址
     * @return 如果邮箱格式有效，返回true；否则返回false
     */
    boolean isValidEmail(String email);
    
    /**
     * 验证密码强度是否足够
     * 
     * @param password 待验证的密码
     * @return 如果密码强度足够，返回true；否则返回false
     */
    boolean isValidPassword(String password);

    /**
     * 用户登录
     * 
     * @param request 登录请求对象，包含用户名和密码
     * @return 登录响应，包含认证状态、消息和令牌
     */
    LoginResponse login(LoginRequest request);
}