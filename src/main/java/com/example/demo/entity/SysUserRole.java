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
@TableName("t_sys_user_role")
public class SysUserRole {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long roleId;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}