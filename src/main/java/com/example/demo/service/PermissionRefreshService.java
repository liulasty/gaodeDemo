package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 权限刷新服务
 * 用于动态刷新系统中的权限配置
 */
@Service
@RequiredArgsConstructor
public class PermissionRefreshService {
    private final DynamicSecurityMetadataSource securityMetadataSource;
    private final DynamicPermissionService permissionService;

    /**
     * 刷新权限配置
     * 当权限发生变化时调用此方法
     */
    public void refreshPermissions() {
        // 重新加载权限定义
        securityMetadataSource.loadResourceDefine();
    }

    /**
     * 添加新的权限配置并刷新
     * @param path 请求路径
     * @param method HTTP方法
     * @param roles 所需角色列表
     */
    public void addPermissionAndRefresh(String path, String method, String... roles) {
        // 调用权限服务添加新的权限
        permissionService.addPermission(path, method, roles);
        // 刷新权限配置
        refreshPermissions();
    }
} 