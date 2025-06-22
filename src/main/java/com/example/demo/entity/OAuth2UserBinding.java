package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * OAuth2用户绑定实体类
 * 用于关联外部OAuth2用户与本地用户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("oauth2_user_binding")
public class OAuth2UserBinding {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 本地用户ID
     */
    private Long userId;
    
    /**
     * 认证提供商(如github, google等)
     */
    private String provider;
    
    /**
     * 提供商用户ID
     */
    private String providerUserId;
    
    /**
     * 提供商用户名
     */
    private String providerUsername;
    
    /**
     * 提供商邮箱
     */
    private String providerEmail;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 