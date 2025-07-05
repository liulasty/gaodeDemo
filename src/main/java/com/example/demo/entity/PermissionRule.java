package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@TableName("sys_permission_rule")
public class PermissionRule {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String pattern;          // 如 "/api/user/**"
    private String httpMethod;       // 如 "GET"（可为空）
    private String requiredRoles;    // 如 "ADMIN,USER"
    private boolean isPublic;        // 是否公开访问
    private boolean enabled;         // 是否启用
}