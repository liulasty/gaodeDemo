package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试用户详情服务实现类
 * 用于处理测试用户认证
 * 注意：此实现仅用于开发测试，生产环境应从数据库加载用户
 * @author Administrator
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

//    // 使用内存中的用户映射表进行测试
//    private static final Map<String, UserDetails> USERS_MAP = new HashMap<>();
//
//    static {
//        // 初始化测试用户
//        // 生产环境中应从数据库中加载用户并使用加密密码
//        List<SimpleGrantedAuthority> userAuthorities = new ArrayList<>();
//        userAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//
//        List<SimpleGrantedAuthority> adminAuthorities = new ArrayList<>();
//        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//
//        // 添加普通用户
//        USERS_MAP.put("user", new User(
//                "user",
//                "{noop}password", // {noop}表示不加密的密码
//                true, true, true, true,
//                userAuthorities
//        ));
//
//        // 添加管理员
//        USERS_MAP.put("admin", new User(
//                "admin",
//                "{noop}admin", // {noop}表示不加密的密码
//                true, true, true, true,
//                adminAuthorities
//        ));
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.demo.entity.User userEntity = userMapper.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 直接从数据库查询用户角色，提高性能
        List<Role> roles = roleMapper.findByUserId(userEntity.getId());
        userEntity.setRoles(roles);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (!roles.isEmpty()) {
            roles.forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()))
            );
        }

        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNonExpired(),
                userEntity.isCredentialsNonExpired(),
                userEntity.isAccountNonLocked(),
                authorities
        );
    }
} 