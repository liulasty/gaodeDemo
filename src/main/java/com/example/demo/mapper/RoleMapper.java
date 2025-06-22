package com.example.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色映射器
 *
 * @author lz
 * @date 2025/04/20 13:14:06
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 按权限 ID 查找
     *
     * @param id 身份证
     *
     * @return {@code List<Role> }
     */
    List<Role> findByPermissionId(Long id);

    /**
     * 根据角色名称查找角色
     *
     * @param roleName 角色名称
     * @return {@code Role}
     */
    Role findByName(String roleName);
    
    /**
     * 根据用户ID查找用户拥有的所有角色
     *
     * @param userId 用户ID
     * @return {@code List<Role>} 角色列表
     */
    List<Role> findByUserId(@Param("userId") Integer userId);
}