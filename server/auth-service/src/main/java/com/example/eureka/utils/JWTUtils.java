package com.example.eureka.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JWTUtils {
    final SecretKey secretKey = Keys.hmacShaKeyFor("chauhoangtanchauhoangtan13081308".getBytes());
    static final long EXPIRE_TIME_ACCESS_TOKEN  = 1000L * 60 * 60;
    static final long EXPIRE_TIME_REFRESH_TOKEN = 1000L * 60 * 60 * 24 * 7;
    private static final String USER_NAME = "username";
    private static final String ROLES = "roles";

    public String generateAccessToken(String userId, String username, List<String> roles) {
        return Jwts
                .builder()
                .setSubject(userId)
                .claim(USER_NAME, username)
                .claim(ROLES, roles)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME_ACCESS_TOKEN))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String userId, String username, List<String> roles) {
        return Jwts
                .builder()
                .setSubject(userId)
                .claim(USER_NAME, username)
                .claim(ROLES, roles)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME_REFRESH_TOKEN))
                .signWith(secretKey)
                .compact();
    }

    public String extractId(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String extractUsername(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (String) claims.get(USER_NAME);
    }

    public Date expiredDate(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public boolean isExpired(String token) {
        return expiredDate(token).before(new Date());
    }

    public boolean validateToken(String token) {
        return extractUsername(token) != null && !isExpired(token);
    }

    public List<String> getRoles(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObject = claims.get(ROLES);
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        return List.of();
    }

    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
