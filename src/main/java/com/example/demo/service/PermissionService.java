package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    /**
     * 添加新的权限
     *
     * @param path   请求路径
     * @param method HTTP方法
     * @param roles  角色列表
     */
    void addPermission(String path, String method, String... roles);

    /**
     * 根据角色名查找权限
     *
     * @param roleName 角色名
     * @return 权限列表
     */
    List<Permission> findByRole(String roleName);

    /**
     * 根据角色名列表查找权限
     *
     * @param roleNames 角色名列表
     * @return 权限列表
     */
    List<Permission> findByRoles(List<String> roleNames);
}