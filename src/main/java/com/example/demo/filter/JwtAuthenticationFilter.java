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

import java.io.IOException;

/**
 * JWT认证过滤器
 * 1. 从请求头中提取JWT令牌
 * 2. 验证JWT有效性
 * 3. 设置认证上下文
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
    ) throws ServletException, IOException {
        // 1. 从Authorization头中获取JWT令牌
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 提取纯令牌（去掉"Bearer "前缀）
        final String jwt = authHeader.substring(7);

        // 3. 从JWT中提取用户名
        final String username = jwtService.extractUsername(jwt);

        // 4. 如果用户名不为空且当前上下文没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 5. 从数据库加载用户详情
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. 验证令牌是否有效
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 7. 创建认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // 8. 设置请求详情
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. 更新安全上下文
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. 继续过滤器链
        filterChain.doFilter(request, response);
    }
}
