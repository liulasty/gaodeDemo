package com.example.demo.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.demo.dto.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * JWT 身份验证入口点
 *
 * @author lz
 * @date 2025/04/13 22:06:25
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String ORIGINAL_REQUEST_URI_ATTRIBUTE =
            "jakarta.servlet.error.request_uri";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            // 获取原始请求路径（优先从错误属性中获取）
            String path = Optional.ofNullable(request.getAttribute(ORIGINAL_REQUEST_URI_ATTRIBUTE))
                    .map(Object::toString)
                    .orElseGet(request::getRequestURI);

            // 构建响应数据
            CommonResponse<Object> responseBody = new CommonResponse<>(
                    401,
                    "未授权: " + getExceptionMessage(authException),
                    path
            );

            // 序列化响应
            new ObjectMapper().writeValue(response.getWriter(), responseBody);
        } catch (IOException e) {
            log.error("响应写入失败", e);
            throw e;
        }
    }

    private String getExceptionMessage(AuthenticationException ex) {
        if (ex == null || ex.getMessage() == null) {
            return "认证失败";
        }

        // 过滤掉Spring Security默认的冗长消息
        return ex.getMessage().replace("Full authentication is required to access this resource", "");
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