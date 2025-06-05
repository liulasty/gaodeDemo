package com.example.demo.controller;

import com.example.demo.entity.Permission;
import com.example.demo.service.PermissionRefreshService;
import com.example.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 定义一个控制器类，处理与权限相关的HTTP请求
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    // 自动注入PermissionService实例，用于权限相关的业务操作
    private final PermissionService permissionService;
    private final PermissionRefreshService permissionRefreshService;

    /**
     * 获取所有权限
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Permission> getAllPermissions() {
        // 调用service方法获取所有权限列表并返回
        return permissionService.list();
    }

    /**
     * 根据角色获取权限
     */
    @GetMapping("/by-role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Permission> getPermissionsByRole(@PathVariable String roleName) {
        return permissionService.findByRole(roleName);
    }

    /**
     * 动态添加新的权限
     */
    @PostMapping("/dynamic")
    @PreAuthorize("hasRole('ADMIN')")
    public void addDynamicPermission(@RequestBody Map<String, Object> request) {
        String path = (String) request.get("path");
        String method = (String) request.get("method");
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) request.get("roles");
        
        permissionRefreshService.addPermissionAndRefresh(
            path, 
            method, 
            roles.toArray(new String[0])
        );
    }

    /**
     * 刷新权限配置
     */
    @PostMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public void refreshPermissions() {
        permissionRefreshService.refreshPermissions();
    }

    // 响应POST请求，添加新的权限信息
    @PostMapping
    public boolean addPermission(@RequestBody Permission permission) {
        // 调用service方法保存权限信息，返回保存结果
        return permissionService.save(permission);
    }

    // 响应PUT请求，更新权限信息
    @PutMapping
    public boolean updatePermission(@RequestBody Permission permission) {
        // 调用service方法根据ID更新权限信息，返回更新结果
        return permissionService.updateById(permission);
    }

    // 响应DELETE请求，删除指定ID的权限信息
    @DeleteMapping("/{id}")
    public boolean deletePermission(@PathVariable Long id) {
        // 调用service方法根据ID删除权限信息，返回删除结果
        return permissionService.removeById(id);
    }
}