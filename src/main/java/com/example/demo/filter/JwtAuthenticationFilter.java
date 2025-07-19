package com.example.demo.filter;

import com.example.demo.service.impl.TokenBlacklistService;
import com.nimbusds.jose.proc.BadJOSEException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.demo.service.JwtService;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 1. 从请求头中提取JWT令牌
 * 2. 验证JWT有效性
 * 3. 设置认证上下文
 * @author lz
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtDecoder jwtDecoder;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            // 1. 检查是否为公开接口（登录、刷新token、公开端点等）
            if (isPublicEndpoint(request)) {
                continueFilterChain(filterChain, request, response);
                return;
            }


            // 2. 从Authorization头中获取JWT令牌
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Authorization 标头缺失或无效");
               throw new ServletException("Authorization 标头缺失或无效");
            }

            // 3. 提取纯令牌（去掉"Bearer "前缀）
            final String token = extractJwt(authHeader);


            if (token != null && blacklistService.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token已失效");
                return;
            }

            // 4. 验证令牌并提取用户名
            String username;
            try {
                username = extractUsernameFromJwt(token);
                // 验证令牌有效性
                jwtService.validateToken(token);
            } catch (ExpiredJwtException e) {
                log.warn("Token expired: {}", e.getMessage());
                handleException(e, response, "Token已过期", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (MalformedJwtException e) {
                log.warn("Malformed token: {}", e.getMessage());
                handleException(e, response, "Token格式错误", HttpServletResponse.SC_BAD_REQUEST);
                return;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid token: {}", e.getMessage());
                handleException(e, response, "无效的JWT令牌", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // 5. 如果用户名不为空且当前上下文没有认证信息
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Authentication authentication = jwtService.getAuthentication(token);

                
                // 8. 更新安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 记录实际设置的权限
                authentication.getAuthorities().forEach(grantedAuthority -> 
                    log.info("Set authority: {}", grantedAuthority.getAuthority()));
            }

            // 9. 继续过滤器链
            continueFilterChain(filterChain, request, response);
        } catch (Exception e) {
            log.error("JWT认证失败: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            handleException(e, response, "认证失败", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return isPublicEndpoint(request);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        // 使用路径匹配器替代直接字符串比较
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String[] publicEndpoints = {
                "/api/auth/login",
                "/api/auth/refresh-token",
                "/api/auth/register",
                "/api/auth/providers",
                "/api/geocode/ip",
                "/swagger-ui.html",
                "/v3/api-docs",
                "/doc.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/webjars/**",
                "/favicon.ico",
                "/error",
                "/actuator/**",
        };
        for (String endpoint : publicEndpoints) {
            if (pathMatcher.match(endpoint, request.getRequestURI())) {
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
        String username = jwtService.extractUserId(jwt);
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("JWT 令牌无效：缺少用户名");
        }
        return username;
    }



    // 继续过滤器链
    private void continueFilterChain(FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }


    private void handleException(Exception e, HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(message);
        throw new JwtException( message);
    }
}