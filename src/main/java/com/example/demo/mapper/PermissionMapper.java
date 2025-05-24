package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

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
     * 查找全部
     *
     * @return {@code List<Permission> }
     */
    List<Permission> findAll();
}