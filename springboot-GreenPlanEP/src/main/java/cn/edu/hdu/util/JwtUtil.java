// src/main/java/cn/edu/hdu/util/JwtUtil.java
package cn.edu.hdu.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component // ← 让 Spring 管理这个类，以便注入配置
public class JwtUtil {

    // 静态字段（供 static 方法使用）
    private static String SECRET_KEY;
    private static long JWT_EXPIRATION;

    // Spring 注入配置（非静态）
    @Value("${app.jwt.secret:my-secret-key-for-greenplan-ep-2026}")
    private String secret;

    @Value("${app.jwt.expiration:604800000}") // 默认7天
    private long expiration;

    // 在 Spring 初始化完成后，将配置赋值给静态字段
    @PostConstruct
    public void init() {
        JwtUtil.SECRET_KEY = this.secret;
        JwtUtil.JWT_EXPIRATION = this.expiration;
    }

    // 生成 Key 对象（使用 HS256 算法）
    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * 生成 JWT Token
     */
    public static String generateToken(String subject) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从 Token 中提取用户名
     */
    public static String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从 Token 中提取任意 Claim
     */
    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析 Token 并获取所有 Claims
     */
    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断 Token 是否已过期
     */
    public static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 从 Token 中获取过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 验证 Token 是否有效
     */
    public static Boolean validateToken(String token, String username) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }
}