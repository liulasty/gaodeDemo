package com.example.demo.handler;

import com.example.demo.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * OAuth2认证成功处理器
 * 在用户通过OAuth2认证后处理登录成功情况
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 获取OAuth2用户信息
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // 从OAuth2用户信息中提取用户标识
        String userId = attributes.get("sub") != null ? attributes.get("sub").toString() : 
                       (attributes.get("id") != null ? attributes.get("id").toString() : 
                       attributes.get("login").toString());
        
        // 创建JWT令牌
        String token = jwtService.generateToken(userId, oAuth2User.getAuthorities());
        
        // 将令牌添加到重定向URL
        String redirectUrl = determineTargetUrl(request, response, authentication);
        if (redirectUrl.contains("?")) {
            redirectUrl += "&token=" + token;
        } else {
            redirectUrl += "?token=" + token;
        }
        
        // 重定向到前端应用
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 这里写你的前端回调地址
        return "http://localhost:5173/oauth2/callback";
    }
} 