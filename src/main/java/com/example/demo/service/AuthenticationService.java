package com.example.demo.service;

import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.SanitizedUser;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidEmailException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    /**
     * 用户注册方法
     * @param request 注册请求对象，包含用户信息
     * @return 认证响应对象，包含JWT令牌
     * @throws IllegalArgumentException 如果邮箱格式无效、密码强度不足或邮箱已被使用
     * @throws RuntimeException 如果注册过程中发生其他异常
     */
    public AuthenticationResponse register(RegisterRequest request) {
        try {
            // 验证邮箱格式
            if (!isValidEmail(request.email())) {
                throw new IllegalArgumentException("邮箱格式无效");
            }
            // 验证密码强度
            if (!isValidPassword(request.password())) {
                throw new IllegalArgumentException("密码强度不足");
            }
            // 检查邮箱是否已被使用
            if (userMapper.findByEmail(request.email()) != null) {
                throw new IllegalArgumentException("该邮箱已被使用");
            }

            // 创建用户对象并设置属性
            User user = User.builder()
                    .firstname(request.firstname())
                    .lastname(request.lastname())
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .role("GUEST")
                    .build();

            // 插入用户到数据库
            userMapper.insert(user);

            // 生成JWT令牌
            var jwtToken = jwtService.generateToken(user);
            return new AuthenticationResponse(jwtToken);
        } catch (Exception e) {
            logger.error("注册过程中发生错误: ", e);
            throw new RuntimeException("注册失败,"+e.getMessage(), e);
        }
    }

    /**
     * 用户认证方法
     * @param request 认证请求对象，包含用户邮箱和密码
     * @return 认证响应对象，包含JWT令牌
     * @throws InvalidEmailException 如果邮箱格式无效
     * @throws UserNotFoundException 如果用户未找到
     * @throws RuntimeException 如果认证过程中发生其他异常
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // 验证邮箱格式
            if (!isValidEmail(request.email())) {
                throw new InvalidEmailException("邮箱格式无效: " + request.email());
            }

            // 使用Spring Security的AuthenticationManager进行认证
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            // 根据邮箱查找用户
            var user = userMapper.findByEmail(request.email());
            if (user == null) {
                throw new UserNotFoundException("未找到邮箱为 " + request.email() + " 的用户");
            }
            Map<String, Object> role = null;
            if (user.getRole() == null) {
                role = Map.of("role", "ADMIN");
            } else {
                role = Map.of("role", user.getRole());
            }

            // 创建SanitizedUser对象
            var sanitizedUser = new SanitizedUser(user.getUsername(), role);

            // 生成JWT令牌
            var jwtToken = jwtService.generateToken(sanitizedUser);
            return new AuthenticationResponse(jwtToken);

        } catch (InvalidEmailException | UserNotFoundException e) {
            logger.error("邮箱 {} 认证过程中发生错误: {}", request.email(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("邮箱 {} 认证过程中发生意外错误: ", request.email(), e);
            throw new RuntimeException("认证失败", e);
        }
    }

    /**
     * 验证邮箱格式是否有效
     * @param email 待验证的邮箱地址
     * @return 如果邮箱格式有效，返回true；否则返回false
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * 验证密码强度是否足够
     * @param password 待验证的密码
     * @return 如果密码强度足够，返回true；否则返回false
     */
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    /**
     * 刷新令牌
     *
     * @param request 请求
     *
     * @return {@code AuthenticationResponse }
     */
    public AuthenticationResponse refreshToken(AuthenticationRequest request) {
        if (jwtService.isTokenValidForUser(request.refreshToken(), request.email())) {
            var user = userMapper.findByEmail(request.email());
            var jwtToken = jwtService.generateToken(user);
            return new AuthenticationResponse(jwtToken);
        }
        return null;
    }

    /**
     * 用户注销方法
     * @param request 认证请求对象，包含用户邮箱和刷新令牌
     */
    public void logout(AuthenticationRequest request) {
        try {
            // 获取刷新令牌
            String refreshToken = request.refreshToken();
            if (refreshToken != null) {
                // 使刷新令牌失效
                jwtService.invalidateToken(refreshToken);
                logger.info("刷新令牌已失效: {}", refreshToken);
            }

            // 可以在这里添加其他注销逻辑，如清除会话信息等

            logger.info("用户 {} 已注销", request.email());
        } catch (Exception e) {
            logger.error("注销过程中发生错误: ", e);
            throw new RuntimeException("注销失败", e);
        }
    }
}