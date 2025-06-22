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
 */
@Slf4j
public class AuthenticationUnifiedController {

    /**
     * REST API接口部分 - 提供JSON格式的API响应
     */
    @RestController
    @RequestMapping("/api/auth")
    @RequiredArgsConstructor
    @Tag(name = "认证接口")
    public static class AuthRestController {
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
         * 获取当前登录用户信息 - 方式2: 使用SecurityContextHolder
         */
        @GetMapping("/user-info2")
        public ResponseEntity<Map<String, Object>> getCurrentUserInfo2() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(Map.of("authenticated", false));
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("authenticated", true);
            userInfo.put("username", authentication.getName());
            userInfo.put("authorities", authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList()));
            
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                userInfo.put("userId", user.getId());
                userInfo.put("email", user.getEmail());
                userInfo.put("roles", user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList()));
            }
            
            return ResponseEntity.ok(userInfo);
        }
        
        /**
         * 获取当前登录用户信息 - 方式3: 使用UserContext工具类
         */
        @GetMapping("/user-info3")
        public ResponseEntity<Map<String, Object>> getCurrentUserInfo3() {
            Map<String, Object> userInfo = new HashMap<>();
            User currentUser = userContext.getCurrentUser();
            
            if (currentUser == null) {
                return ResponseEntity.ok(Map.of("authenticated", false));
            }
            
            userInfo.put("authenticated", true);
            userInfo.put("userId", currentUser.getId());
            userInfo.put("username", currentUser.getUsername());
            userInfo.put("email", currentUser.getEmail());
            userInfo.put("roles", currentUser.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList()));
            
            return ResponseEntity.ok(userInfo);
        }
        
        /**
         * 使用注解检查权限 - 只有管理员可访问
         */
        @GetMapping("/admin-only")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Map<String, Object>> adminOnlyEndpoint() {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "您拥有管理员权限，可以访问此端点");
            result.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(result);
        }
        
        /**
         * 使用注解检查权限 - 需要具有特定角色
         */
        @GetMapping("/role/{roleName}")
        @PreAuthorize("hasRole(#roleName)")
        public ResponseEntity<Map<String, Object>> hasRoleEndpoint(@PathVariable String roleName) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "您拥有 " + roleName + " 角色，可以访问此端点");
            result.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(result);
        }
        
        /**
         * 手动检查权限 - 使用UserContext工具类
         */
        @GetMapping("/check-admin")
        public ResponseEntity<Map<String, Object>> checkAdminPermission() {
            Map<String, Object> result = new HashMap<>();
            
            if (userContext.isAdmin()) {
                result.put("isAdmin", true);
                result.put("message", "您拥有管理员权限");
            } else {
                result.put("isAdmin", false);
                result.put("message", "您不是管理员");
            }
            
            return ResponseEntity.ok(result);
        }

        /**
         * 获取所有权限列表 - 仅管理员
         */
        @GetMapping("/permissions")
        @PreAuthorize("hasRole('ADMIN')")
        public List<Permission> getAllPermissions() {
            return permissionService.list();
        }
    }
    
    /**
     * 页面视图控制器部分 - 提供视图模板页面
     */
    @Controller
    @RequiredArgsConstructor
    public static class AuthViewController {
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
} 