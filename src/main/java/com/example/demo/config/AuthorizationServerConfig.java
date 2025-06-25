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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

/**
 * 授权服务器配置类，用于配置OAuth2授权服务器的核心组件和安全设置
 * @author Administrator
 */
@Configuration
@Slf4j
public class AuthorizationServerConfig {
    private static final String RSA_KEY_PAIR_FILE = "rsa_key_pair.ser";
    


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
        KeyPair keyPair = loadOrGenerateRsaKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                // 使用固定keyId，避免每次重启变化
                .keyID("fixed-key-id")
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 加载或生成RSA密钥对
     *
     * @return 可用的RSA密钥对
     *
     * 实现逻辑：
     * 1. 尝试从文件系统加载已保存的密钥对
     * 2. 如果不存在则生成新密钥对并保存
     * 3. 出现异常时生成临时密钥对
     */
    private KeyPair loadOrGenerateRsaKeyPair() {
        try {
            Path path = Paths.get(RSA_KEY_PAIR_FILE);
            if (Files.exists(path)) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    return (KeyPair) ois.readObject();
                }
            } else {
                KeyPair keyPair = generateRsaKey();
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                    oos.writeObject(keyPair);
                }
                return keyPair;
            }
        } catch (Exception e) {
            log.warn("无法加载/保存RSA密钥对，将使用临时生成的密钥对", e);
            return generateRsaKey();
        }
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
     * 创建自定义JWT解码器
     * @return 配置好的JwtDecoder实例
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("创建JWT解码器");
        // 使用JWK源进行JWT验证（RS256）
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource());
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
