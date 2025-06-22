package com.example.demo.service;

import com.example.demo.entity.OAuth2UserBinding;

/**
 * OAuth2用户绑定服务接口
 */
public interface OAuth2UserBindingService {
    
    /**
     * 保存OAuth2用户绑定信息
     * 
     * @param binding 绑定信息
     * @return 保存后的绑定信息
     */
    OAuth2UserBinding save(OAuth2UserBinding binding);
    
    /**
     * 根据提供商和提供商用户ID查询绑定信息
     * 
     * @param provider 提供商
     * @param providerUserId 提供商用户ID
     * @return 用户绑定信息
     */
    OAuth2UserBinding findByProviderAndProviderUserId(String provider, String providerUserId);
    
    /**
     * 查询用户的OAuth2绑定
     * 
     * @param userId 本地用户ID
     * @return 用户绑定信息
     */
    OAuth2UserBinding findByUserId(Long userId);
    
    /**
     * 解除OAuth2用户绑定
     * 
     * @param binding 绑定信息
     * @return 是否成功
     */
    boolean unbind(OAuth2UserBinding binding);
} 