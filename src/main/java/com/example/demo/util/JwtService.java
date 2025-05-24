package com.example.demo.util;

import com.example.demo.dto.SanitizedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * JWT服务类，用于处理与JWT令牌相关的操作，如生成令牌、解析令牌等
 */
@Slf4j
@Service
public class JwtService {

    // 从配置文件中注入JWT密钥
    @Value("${jwt.secret-key}")
    private String secretKey;

    // 从配置文件中注入JWT过期时间
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // 从配置文件中注入JWT刷新令牌的过期时间
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // 存储黑名单中的令牌及其过期时间
    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    /**
     * 从JWT令牌中提取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从JWT令牌中提取特定的声明
     *
     * @param token       JWT令牌
     * @param claimsResolver 用于解析声明的函数
     * @param <T>         声明的类型
     * @return 解析后的声明
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 生成JWT令牌
     *
     * @param userDetails 用户详细信息
     * @return JWT令牌
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * 生成带有额外声明的JWT令牌
     *
     * @param extraClaims    额外的声明
     * @param userDetails 用户详细信息
     * @return JWT令牌
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * 生成刷新令牌
     *
     * @param userDetails 用户详细信息
     * @return 刷新令牌
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * 构建JWT令牌或刷新令牌
     *
     * @param extraClaims    额外的声明
     * @param userDetails 用户详细信息
     * @param expiration    令牌过期时间
     * @return 构建的令牌
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * 检查令牌是否有效
     *
     * @param token       JWT令牌
     * @param userDetails 用户详细信息
     * @return 令牌是否有效
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * 检查令牌是否过期
     *
     * @param token JWT令牌
     * @return 令牌是否过期
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 从JWT令牌中提取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从JWT令牌中提取所有声明
     *
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 为SanitizedUser对象生成JWT令牌
     *
     * @param sanitizedUser 用户信息
     * @return JWT令牌
     */
    public String generateToken(SanitizedUser sanitizedUser) {
        if (sanitizedUser != null) {
            return generateToken(new HashMap<>(), sanitizedUser);
        }
        return null;
    }

    /**
     * 为SanitizedUser对象生成带有额外声明的JWT令牌
     *
     * @param extraClaims    额外的声明
     * @param sanitizedUser 用户信息
     * @return JWT令牌
     */
    private String generateToken(HashMap<String, Object> extraClaims, SanitizedUser sanitizedUser) {
        if (sanitizedUser != null) {
            return buildToken(extraClaims, sanitizedUser, jwtExpiration);
        }
        return null;
    }

    /**
     * 构建SanitizedUser对象的JWT令牌或刷新令牌
     *
     * @param extraClaims    额外的声明
     * @param sanitizedUser 用户信息
     * @param jwtExpiration 令牌过期时间
     * @return 构建的令牌
     */
    private String buildToken(HashMap<String, Object> extraClaims, SanitizedUser sanitizedUser, long jwtExpiration) {
        if (sanitizedUser != null) {
            return Jwts
                    .builder()
                    .claims(extraClaims)
                    .subject(sanitizedUser.getUsername())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSignInKey())
                    .compact();
        }
        return null;
    }

    /**
     * 检查令牌对用户是否有效
     *
     * @param jwt       JWT令牌
     * @param userDetails 用户详细信息
     * @return 令牌是否有效
     */
    public boolean isTokenValidForUser(String jwt, UserDetails userDetails) {
        if (jwt != null && userDetails != null) {
            String username = extractUsername(jwt);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(jwt);
        }
        return false;
    }

    /**
     * 检查令牌是否有效
     *
     * @param jwt JWT令牌
     * @return 令牌是否有效
     */
    public boolean isTokenValid(String jwt) {
        if (jwt != null) {
            return !isTokenExpired(jwt);
        }
        return false;
    }

    /**
     * 检查令牌对用户是否有效
     *
     * @param jwt  JWT令牌
     * @param email 用户邮箱
     * @return 令牌是否有效
     */
    public boolean isTokenValidForUser(String jwt, String email) {
        if (jwt != null && email != null) {
            String username = extractUsername(jwt);
            return (username.equals(email)) && !isTokenExpired(jwt);
        }
        return false;
    }

    /**
     * 将令牌加入黑名单，使其失效
     *
     * @param refreshToken 刷新令牌
     */
    public void invalidateToken(String refreshToken) {
        tokenBlacklist.put(refreshToken, refreshExpiration);
        log.info("令牌已失效: {}", refreshToken);
    }
}