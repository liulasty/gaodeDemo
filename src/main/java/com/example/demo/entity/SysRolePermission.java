package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("t_sys_role_permission")
public class SysRolePermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long roleId;
    private Long permissionId;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}