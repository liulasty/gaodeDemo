package com.example.demo.service.impl;

import com.example.demo.entity.Role;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

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
                    authorities.add(new SimpleGrantedAuthority(role.getName()))  // 直接使用角色名，不添加前缀
            );
        }
        User user = new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNonExpired(),
                userEntity.isCredentialsNonExpired(),
                userEntity.isAccountNonLocked(),
                authorities
        );
        log.info("用户: {}", user);

        return user;
    }
} 