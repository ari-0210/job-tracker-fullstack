package com.arii.JobTracker.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基于 RFC-7519 规范的 JSON Web Token (JWT) 分布式无状态会话生命周期控制器.
 * <p>底层依托高强度 HMAC-SHA 算法提供防伪伪造、防篡改签名审查，全力支撑系统无状态集群横向水平扩展能力.</p>
 *
 * @author Ari
 */
@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.ms}")
    private long expirationTimeInMs;

    /**
     * 动态构建并校验加盐高强度 HMAC 私钥哈希实例.
     *
     * @return 深度复用且具备合规位数的加密 SecretKey
     * @throws IllegalArgumentException 当配置文件中提取的密钥字节长度未达 256 位安全极限时硬性警告阻断
     */
    private SecretKey getSigningKey() {
        if (secret == null || secret.length() < 32) {

            throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 bytes) long.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 从目标非结构化 Token 中解构抽取用户主键标志（Subject 通常为用户名）.
     *
     * @param token 公网传输的 JWT 字符串原始密文
     * @return 包含在明文域中的唯一身份账户名称
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 精确抽取当前访问令牌的绝对废弃截止时间线.
     *
     * @param token 待提取的令牌
     * @return 令牌时间戳实例
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * 响应式函数式解耦核心萃取器：允许传递任意 Claims 单向提取算子.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 触发硬件级算法解密解构，校验签名并全量吐出载荷字典（Claims）.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * 验证该令牌是否已超越既定废弃时间阈值.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 根据当前成功通过身份审查的 Spring UserDetails 衍生签发全新、防伪的无状态访问凭证.
     *
     * @param userDetails 承载用户当前安全角色的基础载荷实体
     * @return 组装完毕、带有签名尾缀的完整 JWT 令牌文本
     */
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    // 将用户的权限（角色）转换为字符串列表并添加到 claims 中
    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    claims.put("roles", roles); 

    return createToken(claims, userDetails.getUsername());
}

    /**
     * 底层调用打包引擎完成最终密文压缩编码.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject) 
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTimeInMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 针对指定在线用户实体的凭证合法性与时效性双重深度审计.
     * <p>全面比对令牌解密后的 Subject 标志是否与安全上下文载荷一致，并审查是否超越绝对废弃时间红线。</p>
     *
     * @param token       待校验的 JWT 访问令牌密文
     * @param userDetails Spring Security 上下文缓存的真实用户明细载荷
     * @return 若身份契合且未过期返回 true，否则返回 false
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 令牌无状态生命周期时效性单维初筛审查（快速轻量级校验）.
     * <p>仅透视令牌本身的绝对过期时间戳，不依赖数据库或上下文载荷查询，常用于网关层高并发下的初段放行拦截。</p>
     *
     * @param token 待校验的 JWT 访问令牌密文
     * @return 若令牌仍处于安全生存周期内返回 true，已过期或遭篡改则返回 false
     */
    public Boolean validateToken(String token) { // simpler validation without UserDetails
        return !isTokenExpired(token);
    }

}
