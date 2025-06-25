package com.example.demo.service.impl;

import com.example.demo.service.JwtService;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT服务实现类 - 与OAuth2资源服务器兼容
 * 提供JWT令牌的生成、解析和验证功能，支持标准OAuth2格式和旧格式令牌
 * @author Administrator
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    // JWT签名密钥(Base64编码)
    @Value("${jwt.secret}")
    private String secretKey;

    // JWT有效期(毫秒)
    @Value("${jwt.expiration}")
    private Long expiration;

    // JWK密钥源，用于OAuth2标准JWT生成
    @Autowired
    private JWKSource<SecurityContext> jwkSource;

    // OAuth2标准JWT解码器
    @Autowired
    private JwtDecoder jwtDecoder;

    /**
     * 生成JWT令牌(简化版)
     * @param userId 用户唯一标识
     * @param authorities 用户权限集合
     * @return 生成的JWT令牌字符串
     */
    @Override
    public String generateToken(String userId, Collection<? extends GrantedAuthority> authorities) {
        return generateToken(userId, authorities, new HashMap<>());
    }

    /**
     * 从Authentication对象生成JWT令牌
     * @param authentication Spring Security认证对象
     * @return 生成的JWT令牌字符串
     */
    @Override
    public String generateToken(Authentication authentication) {
        String userId;
        Map<String, Object> extraClaims = new HashMap<>();
        
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            userId = attributes.get("sub") != null ? attributes.get("sub").toString() : (attributes.get("id") != null ? attributes.get("id").toString() : attributes.get("login").toString());
            extraClaims.putAll(attributes);
            extraClaims.put("provider", determineProvider(attributes));
        } else {
            userId = authentication.getName();
            extraClaims.put("provider", "local");
        }
        return generateToken(userId, authentication.getAuthorities(), extraClaims);
    }

    /**
     * 生成JWT令牌(完整版)
     * @param userId 用户唯一标识
     * @param authorities 用户权限集合
     * @param extraClaims 额外自定义声明
     * @return 生成的JWT令牌字符串
     */
    @Override
    public String generateToken(String userId, Collection<? extends GrantedAuthority> authorities, Map<String, Object> extraClaims) {
        JwtEncoder encoder = new NimbusJwtEncoder(jwkSource);
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .subject(userId)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(expiration, ChronoUnit.MILLIS))
                .claim("scope", String.join(" ", roles))
                .claim("roles", roles);
        extraClaims.forEach(claimsBuilder::claim);
        return encoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }

    /**
     * 从JWT令牌中提取用户ID
     * @param token JWT令牌字符串
     * @return 用户唯一标识
     */
    @Override
    public String extractUserId(String token) {
        Jwt jwt = null;
        try {
            jwt = jwtDecoder.decode(token);
        } catch (JwtException e) {
            log.error("无效的JWT令牌: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return jwt.getSubject();
    }

    /**
     * 验证JWT令牌有效性
     * @param token JWT令牌字符串
     * @return 验证结果(true表示有效)
     */
    @Override
    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            log.error("无效的JWT令牌: {}", e.getMessage());
            return false;
        }
    }

    // 提取自定义claim的通用方法
    public <T> T extractClaim(String token, String claimName, Class<T> clazz) {
        Jwt jwt = jwtDecoder.decode(token);
        Object claim = jwt.getClaim(claimName);
        return clazz.cast(claim);
    }

    /**
     * 获取签名密钥
     * @return 用于签名的SecretKey对象
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 新增方法：确定OAuth2提供者
    private String determineProvider(Map<String, Object> attributes) {
        if (attributes.containsKey("github_id")) {
            return "github";
        } else if (attributes.containsKey("google_id")) {
            return "google";
        }
        return "unknown";
    }
}
