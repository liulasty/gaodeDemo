package com.example.demo.controller;

import com.example.demo.entity.Permission;
import com.example.demo.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 定义一个控制器类，处理与权限相关的HTTP请求
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    // 自动注入PermissionService实例，用于权限相关的业务操作
    @Autowired
    private PermissionService permissionService;

    // 响应GET请求，获取所有权限信息
    @GetMapping
    public List<Permission> getAllPermissions() {
        // 调用service方法获取所有权限列表并返回
        return permissionService.list();
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