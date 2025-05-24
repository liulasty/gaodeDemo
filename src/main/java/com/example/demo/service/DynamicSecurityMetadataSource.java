package com.example.demo.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 动态安全元数据源
 *
 * @author lz
 * @date 2025/04/20 11:26:08
 */
@Component
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private final DynamicPermissionService permissionService;
    private Map<String, List<ConfigAttribute>> permissionMap;

    /**
     * 构造函数，初始化权限服务并加载资源定义
     * @param permissionService 权限服务接口，用于加载权限信息
     */
    public DynamicSecurityMetadataSource(DynamicPermissionService permissionService) {
        this.permissionService = permissionService;
        loadResourceDefine();
    }
    
    /**
     * 加载资源定义的方法，从权限服务中获取权限配置信息
     */
    public void loadResourceDefine() {
        this.permissionMap = permissionService.loadPermissionDefinitions();
    }
    
    /**
     * 获取所有配置属性的方法，
     * @return 返回配置属性的集合，此处返回null表示不提供配置属性
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        if (permissionMap != null) {
            return permissionMap.values().stream()
                    .flatMap(Collection::stream)
                    .toList();
        }
        return null;
    }
    
    /**
     * 判断是否支持指定类作为过滤对象的方法
     * @param clazz 要检查的类
     * @return 如果指定的类可以作为过滤对象，则返回true，否则返回false
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }


    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        String url = fi.getRequestUrl();
        HttpServletRequest request = fi.getRequest();

        // 尝试匹配"路径:方法"
        List<ConfigAttribute> attributes = permissionMap.get(url + ":" + request.getMethod());
        if (attributes != null) {
            return attributes;
        }

        // 尝试只匹配路径
        attributes = permissionMap.get(url);
        if (attributes != null) {
            return attributes;
        }

        // 没有匹配的权限配置，返回需要认证
        return List.of(new SecurityConfig("ROLE_LOGIN"));
    }
}