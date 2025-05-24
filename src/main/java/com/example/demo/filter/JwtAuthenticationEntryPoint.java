package com.example.demo.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 身份验证入口点
 *
 * @author lz
 * @date 2025/04/13 22:06:25
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        try {
            // 设置响应状态码和内容类型，并指定字符编码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
    
            // 提取异常信息并进行处理
            String errorMessage = "未授权";
            String detailedMessage = (authException != null && authException.getMessage() != null)
                    ? authException.getMessage()
                    : "无法获取详细信息";
            String url = request.getRequestURI();
            
            
            // 构建JSON响应体
            String jsonResponse = String.format(
                    "{ \"error\": \"%s\", \"message\": \"%s\", \"url\": \"%s\" }",
                    escapeJsonString(errorMessage),
                    escapeJsonString(detailedMessage),
                    escapeJsonString(url)
            );
    
            // 写入响应体
            response.getWriter().write(jsonResponse);
    
        } catch (IOException e) {
            log.error("Error writing response: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 转义JSON字符串中的特殊字符
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

}