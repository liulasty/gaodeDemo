package com.example.demo.service.impl;

import com.example.demo.entity.OAuth2UserBinding;
import com.example.demo.mapper.OAuth2UserBindingMapper;
import com.example.demo.service.OAuth2UserBindingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth2用户绑定服务实现类
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserBindingServiceImpl implements OAuth2UserBindingService {
    
    private final OAuth2UserBindingMapper oauth2UserBindingMapper;
    
    @Override
    @Transactional
    public OAuth2UserBinding save(OAuth2UserBinding binding) {
        if (binding.getId() == null) {
            oauth2UserBindingMapper.insert(binding);
        } else {
            oauth2UserBindingMapper.updateById(binding);
        }
        return binding;
    }
    
    @Override
    public OAuth2UserBinding findByProviderAndProviderUserId(String provider, String providerUserId) {
        return oauth2UserBindingMapper.findByProviderAndProviderUserId(provider, providerUserId);
    }
    
    @Override
    public OAuth2UserBinding findByUserId(Long userId) {
        return oauth2UserBindingMapper.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public boolean unbind(OAuth2UserBinding binding) {
        if (binding.getId() != null) {
            return oauth2UserBindingMapper.deleteById(binding.getId()) > 0;
        }
        return false;
    }
} 