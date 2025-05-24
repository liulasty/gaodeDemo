package com.example.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.demo.util.JwtService;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 1. 从请求头中提取JWT令牌
 * 2. 验证JWT有效性
 * 3. 设置认证上下文
 * @author lz
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            // 1. 检查是否为公开接口（登录、刷新token、公开端点等）
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            // 1. 从Authorization头中获取JWT令牌
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                continueFilterChain(filterChain, request, response);
                return;
            }

            // 2. 提取纯令牌（去掉"Bearer "前缀）
            final String jwt = extractJwt(authHeader);

            // 3. 验证令牌是否有效
            if (!jwtService.isTokenValid(jwt)) {
                continueFilterChain(filterChain, request, response);
                return;
            }

            // 4. 从JWT中提取用户名
            final String username = extractUsernameFromJwt(jwt);

            // 5. 如果用户名不为空且当前上下文没有认证信息
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // 6. 从数据库加载用户详情并验证令牌
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (!jwtService.isTokenValidForUser(jwt, userDetails)) {
                    continueFilterChain(filterChain, request, response);
                    return;
                }

                // 7. 创建认证令牌
                UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(userDetails, request);

                // 8. 更新安全上下文
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            // 9. 继续过滤器链
            continueFilterChain(filterChain, request, response);
        } catch (Exception e) {
            handleException(e, response);
        }
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        // 添加公开端点的URL列表
        String[] publicEndpoints = {
                "/api/auth/login",
                "/api/auth/refresh-token",
                "/api/geocode/address",
                "/api/geocode/ip",
                "/swagger-ui.html",
                "/v3/api-docs",
            };
        for (String endpoint : publicEndpoints) {
            if (request.getRequestURI().equals(endpoint)) {
                return true;
            }
        }
        return false;
    }

    // 提取JWT令牌
    private String extractJwt(String authHeader) {
        if (authHeader.length() < 7) {
            throw new IllegalArgumentException("无效的 Authorization 标头格式");
        }
        return authHeader.substring(7);
    }

    // 从JWT中提取用户名
    private String extractUsernameFromJwt(String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("JWT 令牌无效：缺少用户名");
        }
        return username;
    }

    // 创建认证令牌
    private UsernamePasswordAuthenticationToken createAuthenticationToken(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authToken;
    }

    // 继续过滤器链
    private void continueFilterChain(FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

    // 异常处理
    private void handleException(Exception e, HttpServletResponse response) throws IOException {
        logger.error("发生异常: {}", e);
        // 返回适当的HTTP状态码
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}