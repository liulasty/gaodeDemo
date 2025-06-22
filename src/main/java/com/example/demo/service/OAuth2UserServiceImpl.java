package com.example.demo.service;

import com.example.demo.entity.OAuth2UserBinding;
import com.example.demo.entity.Role;
import com.example.demo.entity.SysUserRole;
import com.example.demo.entity.User;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.SysUserRoleMapper;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2用户服务实现
 * 处理从OAuth2提供商获取的用户信息
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 通过代理获取OAuth2用户
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        
        // 提取注册ID (provider)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        // 根据提供商处理不同的属性映射
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        
        // 不同提供商有不同的用户属性结构
        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        
        // 提取电子邮件
        String email = extractEmail(registrationId, attributes);
        // 提取用户名
        String username = extractUsername(registrationId, attributes);
        // 提取提供商用户ID
        String providerUserId = attributes.get(nameAttributeKey).toString();
        
        // 查找或创建用户
        User user = findOrCreateUser(registrationId, email, username, providerUserId);
        
        // 收集权限
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 加载用户角色
        if (user.getRoles() != null) {
            // 添加角色权限
            for (Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        } else {
            // 如果没有角色，添加默认权限
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        // 向属性中添加用户信息
        attributes.put("user", user);
        attributes.put("provider", registrationId);
        
        // 创建OAuth2用户
        return new DefaultOAuth2User(
                authorities,
                attributes,
                nameAttributeKey
        );
    }
    
    /**
     * 根据提供商和用户信息查找或创建用户
     */
    private User findOrCreateUser(String provider, String email, String username, String providerUserId) {
        // 先根据Email查找用户
        User user = null;
        if (email != null) {
            user = userMapper.findByEmail(email);
        }
        
        // 如果用户不存在，创建新用户
        if (user == null) {
            // 创建新用户
            user = User.builder()
                    .username( username)
                    .email(email)
                    .status(1) // 状态正常
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            userMapper.insert(user);
            
            // 分配默认角色
            Role userRole = roleMapper.findByName("USER");
            if (userRole != null) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(user.getId().longValue());
                sysUserRole.setRoleId(userRole.getId());
                sysUserRole.setCreatedAt(LocalDateTime.now());
                sysUserRoleMapper.insert(sysUserRole);
                
                // 设置角色
                user.setRoles(Collections.singletonList(userRole));
            }
        }
        
        // 加载用户角色
        loadUserRoles(user);
        
        return user;
    }
    
    /**
     * 从属性中提取电子邮件
     */
    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("github".equals(provider)) {
            return (String) attributes.get("email");
        } else if ("google".equals(provider)) {
            return (String) attributes.get("email");
        }
        return null;
    }
    
    /**
     * 从属性中提取用户名
     */
    private String extractUsername(String provider, Map<String, Object> attributes) {
        if ("github".equals(provider)) {
            return (String) attributes.get("login");
        } else if ("google".equals(provider)) {
            return (String) attributes.get("name");
        }
        return "user_" + System.currentTimeMillis(); // 默认用户名
    }
    
    /**
     * 加载用户角色
     */
    private void loadUserRoles(User user) {
        if (user != null && user.getId() != null) {
            // 查询用户角色
            List<Role> roles = roleMapper.findByUserId(user.getId());
            user.setRoles(roles);
        }
    }
} 