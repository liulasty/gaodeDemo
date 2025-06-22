package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RegisterResponse;
import com.example.demo.dto.SanitizedUser;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.SysUserRole;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidEmailException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.SysUserRoleMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    /**
     * 实现注册新用户方法
     * @param request 注册请求
     * @return 注册结果响应
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 检查参数有效性
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("用户名不能为空")
                    .build();
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("邮箱不能为空")
                    .build();
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("密码不能为空")
                    .build();
        }
        
        // 检查密码和确认密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("密码和确认密码不一致")
                    .build();
        }
        
        // 验证邮箱格式
        if (!isValidEmail(request.getEmail())) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("邮箱格式无效")
                    .build();
        }
        
        // 验证密码强度
        if (!isValidPassword(request.getPassword())) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("密码强度不足，至少需要8个字符")
                    .build();
        }
        
        // 检查邮箱是否已存在
        if (isEmailExists(request.getEmail())) {
            return RegisterResponse.builder()
                    .success(false)
                    .message("该邮箱已被注册")
                    .build();
        }

        try {
            // 创建用户对象
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .status(1)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            // 保存用户
            userMapper.insert(user);
            
            // 分配默认用户角色
            Role userRole = roleMapper.findByName("USER");
            if (userRole != null) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(user.getId().longValue());
                sysUserRole.setRoleId(userRole.getId());
                sysUserRole.setCreatedAt(LocalDateTime.now());
                sysUserRoleMapper.insert(sysUserRole);
                
                // 查询角色的权限并设置
                List<Role> roles = Collections.singletonList(userRole);
                user.setRoles(roles);
                
                // 获取用户权限
                List<Permission> permissions = permissionMapper.findPermissionsByUserId(user.getId().longValue());
                user.setPermissions(permissions);
            }
            
            // 创建认证对象，正确使用UsernamePasswordAuthenticationToken构造函数
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
            
            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成JWT令牌
            String token = jwtService.generateToken(authentication);
            
            // 返回注册成功响应
            return RegisterResponse.builder()
                    .success(true)
                    .message("注册成功")
                    .userId(user.getId().longValue())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("注册失败,"+e.getMessage(), e);
        }
    }
    
    /**
     * 用户登录认证
     *
     * @param request 认证请求对象，包含用户邮箱和密码
     * @return 认证响应对象，包含JWT令牌
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            String identifier = request.email();
            User user = null;
            

            // 如果不是有效的邮箱格式，尝试作为用户名查询
            user = userMapper.findByUsername(identifier);
            if (user == null) {
                throw new UserNotFoundException("未找到用户名为 " + identifier + " 的用户");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.password()
                    )
            );
            
            // 加载用户角色和权限
            loadUserRoles(user);
            
            // 创建认证对象
            List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .collect(Collectors.toList());
            
            Authentication userAuth = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, authorities);

            // 生成JWT令牌
            String jwtToken = jwtService.generateToken(userAuth);
            
            return new AuthenticationResponse(jwtToken);

        } catch (UserNotFoundException e) {
            logger.error("认证过程中发生错误: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("认证过程中发生意外错误: ", e);
            throw new RuntimeException("认证失败", e);
        }
    }
    
    /**
     * 刷新令牌
     *
     * @param request 请求对象，包含邮箱和刷新令牌
     * @return 认证响应对象，包含新的JWT令牌
     */
    @Override
    public AuthenticationResponse refreshToken(AuthenticationRequest request) {
        // 验证令牌有效性
        if (jwtService.validateToken(request.refreshToken())) {
            // 验证用户
            User user = findByEmail(request.email());
            
            // 创建认证对象
            List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .collect(Collectors.toList());
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, authorities);
            
            // 生成新令牌
            String jwtToken = jwtService.generateToken(authentication);
            return new AuthenticationResponse(jwtToken);
        }

        return null;
    }
    
    /**
     * 用户注销
     *
     * @param request 认证请求对象，包含用户邮箱和刷新令牌
     */
    @Override
    public void logout(AuthenticationRequest request) {
        try {
            // 在这里可以添加令牌黑名单等注销逻辑
            
            // 清除安全上下文
            SecurityContextHolder.clearContext();

            logger.info("用户 {} 已注销", request.email());
        } catch (Exception e) {
            logger.error("注销过程中发生错误: ", e);
            throw new RuntimeException("注销失败", e);
        }
    }
    
    /**
     * 验证邮箱格式是否有效
     * 
     * @param email 待验证的邮箱地址
     * @return 如果邮箱格式有效，返回true；否则返回false
     */
    @Override
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * 验证密码强度是否足够
     * 
     * @param password 待验证的密码
     * @return 如果密码强度足够，返回true；否则返回false
     */
    @Override
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        return userMapper.findByEmail(email) != null;
    }
    
    @Override
    public User findById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new UserNotFoundException("用户不存在，ID: " + id);
        }
        loadUserRoles(user);
        return user;
    }
    
    @Override
    public User findByEmail(String email) {
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("用户不存在，邮箱: " + email);
        }
        loadUserRoles(user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("用户不存在，用户名: " + username);
        }
        loadUserRoles(user);
        return user;
    }

    /**
     * 加载用户角色和权限
     * @param user 用户对象
     */
    private void loadUserRoles(User user) {
        if (user != null) {
            // 加载用户角色
            List<Role> roles = getUserRoles(user.getId());
            user.setRoles(roles);
            
            // 加载用户权限
            List<Permission> permissions = getUserPermissions(user.getId());
            user.setPermissions(permissions);
        }
    }
    
    /**
     * 获取用户角色
     * @param userId 用户ID
     * @return 角色列表
     */
    private List<Role> getUserRoles(Integer userId) {
        // 实现获取用户角色的逻辑
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(wrapper);
        
        if (userRoles != null && !userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .toList();
            
            // 查询角色信息
            LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(Role::getId, roleIds);
            return roleMapper.selectList(roleWrapper);
        }
        
        return Collections.emptyList();
    }
    
    /**
     * 获取用户权限
     * @param userId 用户ID
     * @return 权限列表
     */
    private List<Permission> getUserPermissions(Integer userId) {
        // 获取用户权限的逻辑
        return permissionMapper.findPermissionsByUserId(userId.longValue());
    }

    /**
     * 用户登录
     *
     * @param request 登录请求对象，包含用户名和密码
     * @return 登录响应，包含认证状态、消息和令牌
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // 创建认证请求
            AuthenticationRequest authRequest = new AuthenticationRequest(
                    request.getUsername(), request.getPassword());
            
            // 使用authenticate方法进行认证
            AuthenticationResponse authResponse = authenticate(authRequest);
            
            if (authResponse != null && authResponse.token() != null) {
                // 从安全上下文获取认证信息
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                
                // 返回成功响应
                return new LoginResponse(
                        true,
                        "登录成功",
                        authResponse.token(),
                        authentication.getName(),
                        authentication.getAuthorities()
                );
            } else {
                // 认证失败
                return new LoginResponse(
                        false,
                        "认证失败",
                        null,
                        null,
                        null
                );
            }
        } catch (Exception e) {
            logger.error("登录失败: {}", e.getMessage());
            // 认证失败
            return new LoginResponse(
                    false,
                    "用户名或密码不正确: " + e.getMessage(),
                    null,
                    null,
                    null
            );
        }
    }
} 