package com.yangyang.java.ai.langchain4j.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

// 10.9 新增 生成和解析 token
public class JwtUtils {

    private static final String SECRET_KEY = "mySecretKeyForDoctorSystem1234567890";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24;     // 24 h
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 生成 token
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 token
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
