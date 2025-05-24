package com.example.demo.service;

/*
 * Created with IntelliJ IDEA.
 * @Author: lz
 * @Date: 2025/04/13/19:22
 * @Description:
 */

import com.example.demo.entity.Token;
import com.example.demo.mapper.TokenMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * @author lz
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenMapper tokenMapper;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // 1. 从请求头中获取Authorization字段
        final String authHeader = request.getHeader("Authorization");

        // 2. 检查Authorization头是否存在且格式正确
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // 如果没有有效的Authorization头，直接返回
        }

        // 3. 提取JWT令牌(去掉"Bearer "前缀)
        final String jwt = authHeader.substring(7);

        // 4. 根据令牌从数据库查找对应的令牌记录
        Token token = tokenMapper.findByToken(jwt);


        // 5. 如果找到令牌记录，则将其标记为已过期和已撤销
        if (token != null) {
            token.setExpired(true);
            token.setRevoked(true);
            tokenMapper.updateToken(token);

            // 可选: 清除SecurityContext
            SecurityContextHolder.clearContext();
        }
    }
}