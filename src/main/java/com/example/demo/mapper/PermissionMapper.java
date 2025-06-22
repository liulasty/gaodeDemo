package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限映射器
 *
 * @author lz
 * @date 2025/04/20 11:29:29
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 查找全部权限
     *
     * @return {@code List<Permission>} 权限列表
     */
    List<Permission> findAll();

    /**
     * 根据角色名查找权限
     *
     * @param roleName 角色名
     * @return {@code List<Permission>} 权限列表
     */
    List<Permission> findByRole(@Param("roleName") String roleName);

    /**
     * 根据角色名列表查找权限
     *
     * @param roleNames 角色名列表
     * @return {@code List<Permission>} 权限列表
     */
    List<Permission> findByRoles(@Param("roleNames") List<String> roleNames);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return {@code List<Permission>} 权限列表
     */
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    void addPermission(String path, String method, String[] roles);
}