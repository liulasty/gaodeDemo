package com.example.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 自定义登出处理器
 */
@Service
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // 可以在这里添加登出逻辑，如令牌失效等

        response.setStatus(HttpServletResponse.SC_OK);
        // 例如清除安全上下文已经在SecurityConfig中处理
        SecurityContextHolder.clearContext();
    }
}
