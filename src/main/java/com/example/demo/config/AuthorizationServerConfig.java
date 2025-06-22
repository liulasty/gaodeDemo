package com.example.demo.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * 授权服务器配置类，用于配置OAuth2授权服务器的核心组件和安全设置
 * @author Administrator
 */
@Configuration
@Slf4j
public class AuthorizationServerConfig {

    /**
     * 配置授权服务器的安全过滤器链
     * @param http HttpSecurity对象用于配置安全策略
     * @return 配置好的SecurityFilterChain实例
     * @throws Exception 配置过程中可能抛出的异常
     *
     * 主要功能：
     * 1. 应用默认的OAuth2授权服务器安全配置
     * 2. 启用OpenID Connect 1.0支持
     * 3. 配置认证失败时跳转到登录页面
     * 4. 配置资源服务器使用JWT令牌
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 应用默认的OAuth2授权服务器安全配置
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // 启用OpenID Connect支持
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        // 配置异常处理和资源服务器
        http
            .exceptionHandling(exceptions ->
                exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * 创建JWK(JSON Web Key)源，用于提供JWT签名密钥
     * @return JWKSource实例，包含生成的RSA密钥对
     *
     * 实现细节：
     * 1. 生成2048位的RSA密钥对
     * 2. 构建RSAKey对象并设置密钥ID
     * 3. 创建不可变的JWKSet
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        log.info("创建JWK源");
        // 生成RSA密钥对
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // 构建RSAKey对象
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        // 创建JWK集合并返回不可变视图
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 生成RSA密钥对
     * @return 生成的KeyPair对象
     * @throws IllegalStateException 如果密钥生成失败
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
     * 创建JWT解码器
     * @param jwkSource JWK源，用于验证JWT签名
     * @return 配置好的JwtDecoder实例
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 配置授权服务器设置
     * @return 默认配置的AuthorizationServerSettings实例
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
