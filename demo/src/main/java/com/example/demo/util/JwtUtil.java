package com.example.demo.util;

import io.jsonwebtoken.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
/**
 * @Author
 * @Date
 */
public class JwtUtil {
    private static long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000;
//    test
//    private static long TOKEN_EXPIRATION =  30 * 1000;
    private static String TOKEN_SECRET_KEY = "123456";
    /**
     * 生成Token
     * @param subject   用户名
     * @return
     */
    public static String createToken(String subject) {
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        Date expirationDate = new Date(currentTimeMillis + TOKEN_EXPIRATION);
        //  存放自定义属性，比如用户拥有的权限
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET_KEY)
                .compact();
    }
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
//    private static Claims extractAllClaims(String token) {
//        return Jwts.parser().setSigningKey(TOKEN_SECRET_KEY).parseClaimsJws(token).getBody();
//    }

    // 过期不抛500异常
    private static Claims extractAllClaims(String token) {
        Claims claims;
        try{
            claims =  Jwts.parser().setSigningKey(TOKEN_SECRET_KEY).parseClaimsJws(token).getBody();

        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }
        return claims;
    }
}
