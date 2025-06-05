package com.example.demo.service;

import org.springframework.security.access.SecurityConfig;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.mapper.RoleMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 动态权限服务
 *
 * @author lz
 * @date 2025/04/20 11:25:49
 */
@Service
@RequiredArgsConstructor
public class DynamicPermissionService {
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;

    public Map<String, List<ConfigAttribute>> loadPermissionDefinitions() {
        Map<String, List<ConfigAttribute>> permissionMap = new ConcurrentHashMap<>();
        List<Permission> permissions = permissionMapper.findAll();

        for (Permission permission : permissions) {
            // 获取拥有此权限的角色
            List<Role> roles = roleMapper.findByPermissionId(permission.getId());
            
            List<ConfigAttribute> configAttributes = roles.stream()
                    .map(role -> new SecurityConfig(role.getName()))
                    .collect(Collectors.toList());

            // 构建权限映射，key为"路径:方法"或"路径"
            String key = permission.getMethod() != null ?
                    permission.getPath() + ":" + permission.getMethod() :
                    permission.getPath();

            permissionMap.put(key, configAttributes);
        }

        return permissionMap;
    }

    public void addPermission(String path, String method, String[] roles) {
        permissionMapper.addPermission(path, method, roles);
    }
}