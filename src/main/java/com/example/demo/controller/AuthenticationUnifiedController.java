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
    private final PermissionService permissionService;
    private final PermissionRefreshService permissionRefreshService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = userService.register(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 直接调用 userService.login 方法
        LoginResponse response = userService.login(loginRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    @Autowired
    private ApplicationContext applicationContext;

    @PostMapping("/refresh-permissions")
    public String refreshPermissions() {

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)
                applicationContext.getAutowireCapableBeanFactory();
        beanFactory.destroySingleton("securityFilterChain");


        applicationContext.getBean(SecurityFilterChain.class);

        return "权限规则已刷新！";
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody AuthenticationRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("登出成功");
    }

    /**
     * 刷新令牌接口
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    /**
     * 获取OAuth2认证用户信息
     */
    @GetMapping("/oauth2-user")
    public ResponseEntity<Map<String, Object>> getOAuth2User(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            Jwt o = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("o: {}", o.getClaims());
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("name", principal.getAttribute("name"));
        response.put("email", principal.getAttribute("email"));
        response.put("provider", principal.getAttribute("provider"));
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取可用的OAuth2认证提供商列表
     */
    @GetMapping("/providers")
    public ResponseEntity<Map<String, String>> getOAuth2Providers() {
        Map<String, String> providers = new HashMap<>();
        
        if (clientRegistrationRepository.findByRegistrationId("github") != null) {
            providers.put("github", "/oauth2/authorization/github");
        }
        if (clientRegistrationRepository.findByRegistrationId("google") != null) {
            providers.put("google", "/oauth2/authorization/google");
        }
        
        return ResponseEntity.ok(providers);
    }

    /**
     * 从认证对象生成JWT令牌
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("error", "未认证"));
        }
        
        String token = jwtService.generateToken(authentication);
        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * 检查邮箱是否已被注册
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return ResponseEntity.ok(Map.of(
                "email", email,
                "exists", exists
        ));
    }

    /**
     * 获取当前登录用户信息 - 方式1: 使用Authentication参数
     */
    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getCurrentUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authenticated", true);
        userInfo.put("username", authentication.getName());
        userInfo.put("authorities", authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "login";
    }

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * 主页重定向
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/api/auth/user-info";
        }
        return "redirect:/login";
    }
}