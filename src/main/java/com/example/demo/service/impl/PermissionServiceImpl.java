package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.SysRolePermission;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.mapper.SysRolePermissionMapper;
import com.example.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    
    private final RoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPermission(String path, String method, String... roles) {
        // 1. 创建权限记录
        Permission permission = new Permission();
        permission.setPath(path);
        permission.setMethod(method);
        permission.setName(generatePermissionName(path, method));
        permission.setDescription("动态添加的权限");
        save(permission);

        // 2. 获取或创建角色
        for (String roleName : roles) {
            Role role = roleMapper.findByName(roleName);
            if (role == null) {
                role = new Role();
                role.setName(roleName);
                role.setDescription("动态创建的角色");
                roleMapper.insert(role);
            }

            // 3. 创建角色-权限关联
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(permission.getId());
            rolePermissionMapper.insert(rolePermission);
        }
    }

    @Override
    public List<Permission> findByRole(String roleName) {
        return baseMapper.findByRole(roleName);
    }

    @Override
    public List<Permission> findByRoles(List<String> roleNames) {
        return baseMapper.findByRoles(roleNames);
    }

    private String generatePermissionName(String path, String method) {
        return (method != null ? method + "_" : "") + path.replace("/", "_").toUpperCase();
    }
}