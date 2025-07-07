package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.entity.Permission;
import com.example.demo.service.*;
import com.example.demo.util.UserContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.demo.dto.CommonResponse;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统一认证控制器
 * 整合了所有与认证、用户信息、权限相关的接口
 * @author Administrator
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口")
public class AuthenticationUnifiedController {

    // 服务依赖注入
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserContext userContext;
    private final ApplicationContext applicationContext;
    private final UserDetailsService userDetailsService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public CommonResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = userService.register(request);
        
        if (response.isSuccess()) {
            return new CommonResponse<>(200, "注册成功", response);
        } else {
            return new CommonResponse<>(400, "注册失败", response);
        }
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public CommonResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 直接调用 userService.login 方法
        LoginResponse response = userService.login(loginRequest);
        
        if (response.isSuccess()) {
            return new CommonResponse<>(200, "登录成功", response);
        } else {
            return new CommonResponse<>(401, "登录失败", response);
        }
    }



    @PostMapping("/refresh-permissions")
    public CommonResponse<String> refreshPermissions() {

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)
                applicationContext.getAutowireCapableBeanFactory();
        beanFactory.destroySingleton("securityFilterChain");


        applicationContext.getBean(SecurityFilterChain.class);

        return new CommonResponse<>(200, "权限规则已刷新！", "权限规则已刷新！");
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public CommonResponse<String> logout(@RequestBody AuthenticationRequest request) {
        userService.logout(request);
        return new CommonResponse<>(200, "登出成功", "登出成功");
    }

    /**
     * 刷新令牌接口
     */
    @PostMapping("/refresh-token")
    public CommonResponse<AuthenticationResponse> refreshToken(
            @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = userService.refreshToken(request);
        return new CommonResponse<>(200, "刷新令牌成功", response);
    }

    /**
     * 获取OAuth2认证用户信息
     */
    @GetMapping("/oauth2-user")
    public CommonResponse<Map<String, Object>> getOAuth2User(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            Jwt o = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("o: {}", o.getClaims());
            return new CommonResponse<>(401, "未认证", Map.of("authenticated", false));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("name", principal.getAttribute("name"));
        response.put("email", principal.getAttribute("user"));
        response.put("provider", principal.getAttribute("provider"));
        
        return new CommonResponse<>(200, "获取OAuth2用户信息成功", response);
    }

    /**
     * 获取可用的OAuth2认证提供商列表
     */
    @GetMapping("/providers")
    public CommonResponse<Map<String, String>> getOAuth2Providers() {
        Map<String, String> providers = new HashMap<>();
        
        if (clientRegistrationRepository.findByRegistrationId("github") != null) {
            providers.put("github", "/oauth2/authorization/github");
        }
        if (clientRegistrationRepository.findByRegistrationId("google") != null) {
            providers.put("google", "/oauth2/authorization/google");
        }
        
        return new CommonResponse<>(200, "获取OAuth2认证提供商成功", providers);
    }

    /**
     * 从认证对象生成JWT令牌
     */
    @GetMapping("/token")
    public CommonResponse<Map<String, String>> getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new CommonResponse<>(401, "未认证", Map.of("error", "未认证"));
        }

        log.info("authentication: {}", authentication);
        
        // 从 UserDetailsService 获取原始用户权限
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        String token = jwtService.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
        return new CommonResponse<>(200, "获取token成功", Map.of("token", token));
    }

    /**
     * 检查邮箱是否已被注册
     */
    @GetMapping("/check-email")
    public CommonResponse<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return new CommonResponse<>(200, "查询邮箱成功", Map.of(
                "user", email,
                "exists", exists
        ));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/user-info")
    public CommonResponse<Map<String, Object>> getCurrentUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new CommonResponse<>(401, "未认证", Map.of("authenticated", false));
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authenticated", true);
        userInfo.put("username", authentication.getName());
        userInfo.put("authorities", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        

        
        return new CommonResponse<>(200, "获取当前用户信息成功", userInfo);
    }
}