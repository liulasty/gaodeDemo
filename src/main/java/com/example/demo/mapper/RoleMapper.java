package com.example.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Role;
import org.apache.ibatis.annotations.Mapper;

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

    Role findByName(String roleName);
}