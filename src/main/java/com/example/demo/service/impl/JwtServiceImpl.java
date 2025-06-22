package com.example.demo.service.impl;

import com.example.demo.dto.DefaultClaims;
import com.example.demo.service.JwtService;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
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
        // 处理不同类型的认证主体(OAuth2用户或普通用户)
        String userId;
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            Map<String, Object> attributes = oauth2User.getAttributes();
            // 从OAuth2用户信息中提取用户标识
            userId = attributes.get("sub") != null ? attributes.get("sub").toString() :
                    (attributes.get("id") != null ? attributes.get("id").toString() :
                            attributes.get("login").toString());
        } else {
            userId = authentication.getName();
        }
        return generateToken(userId, authentication.getAuthorities());
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
        // 尝试使用OAuth2标准格式生成JWT
        try {
            JwtEncoder encoder = new NimbusJwtEncoder(jwkSource);

            // 将权限集合转换为角色字符串列表
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // 构建标准JWT声明
            JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                    .subject(userId)
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plus(expiration, ChronoUnit.MILLIS))
                    .claim("scope", String.join(" ", roles));

            // 添加额外声明和角色信息
            extraClaims.forEach(claimsBuilder::claim);
            claimsBuilder.claim("roles", roles);

            // 编码并返回令牌
            return encoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
        } catch (Exception e) {
            log.error("生成JWT令牌失败: {}", e.getMessage(), e);
            // 如果标准格式失败，回退到旧格式
            return generateTokenLegacy(userId, authorities, extraClaims);
        }
    }

    /**
     * 旧版JWT生成方法(兼容性备用)
     * @param userId 用户唯一标识
     * @param authorities 用户权限集合
     * @param extraClaims 额外自定义声明
     * @return 生成的JWT令牌字符串
     */
    private String generateTokenLegacy(String userId, Collection<? extends GrantedAuthority> authorities, Map<String, Object> extraClaims) {
        // 合并额外声明和角色信息
        Map<String, Object> claims = new HashMap<>(extraClaims);
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        claims.put("scope", String.join(" ", roles));

        // 设置令牌有效期
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        // 使用jjwt库构建令牌
        return Jwts.builder()
                .claims(claims)
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从JWT令牌中提取用户ID
     * @param token JWT令牌字符串
     * @return 用户唯一标识
     */
    @Override
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
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

    /**
     * 从令牌中提取指定声明
     * @param token JWT令牌字符串
     * @param claimsResolver 声明解析函数
     * @return 解析后的声明值
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 提取令牌中的所有声明
     * @param token JWT令牌字符串
     * @return 包含所有声明的Claims对象
     */
    private Claims extractAllClaims(String token) {
        try {
            // 使用OAuth2解码器解码令牌
            Jwt decodedJwt = jwtDecoder.decode(token);
            Map<String, Object> claims = decodedJwt.getClaims();

            return decodedJwt.
        } catch (JwtException e) {
            log.error("解析JWT令牌失败: {}", e.getMessage());
            throw new RuntimeException("解析JWT令牌失败", e);
        }
    }

    /**
     * 获取签名密钥
     * @return 用于签名的SecretKey对象
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
