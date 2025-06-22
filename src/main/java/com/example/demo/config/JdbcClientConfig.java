package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

/**
 * OAuth2客户端JDBC配置
 * 使用数据库存储OAuth2客户端信息
 */
@Configuration
public class JdbcClientConfig {

    /**
     * 配置客户端仓库
     * 用于从数据库加载已注册的OAuth2客户端
     * 
     * @param jdbcTemplate JDBC模板
     * @return 注册客户端仓库
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    /**
     * 配置OAuth2授权服务
     * 用于存储授权信息
     * 
     * @param jdbcTemplate JDBC模板
     * @param registeredClientRepository 已注册客户端仓库
     * @return OAuth2授权服务
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(
            JdbcTemplate jdbcTemplate, 
            RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * 配置OAuth2授权同意服务
     * 用于存储用户对客户端的授权同意
     * 
     * @param jdbcTemplate JDBC模板
     * @param registeredClientRepository 已注册客户端仓库
     * @return OAuth2授权同意服务
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            JdbcTemplate jdbcTemplate, 
            RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }
} 