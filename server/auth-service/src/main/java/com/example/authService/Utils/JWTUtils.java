package com.example.authService.Utils;

import com.example.authService.Model.Roles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JWTUtils {
    final SecretKey secretKey = Keys.hmacShaKeyFor("chauhoangtanchauhoangtan13081308".getBytes());
    final long expireTimeAccessToken = 1000 * 60 * 60;
    final long expireTimeRefreshToken = 1000 * 60 * 60 * 24 * 7;

    public String generateAccessToken(String userId, String username, List<String> roles) {
        return Jwts
                .builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("roles", roles)
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeAccessToken))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String userId, String username, List<String> roles) {
        return Jwts
                .builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("roles", roles)
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeRefreshToken))
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

        return (String) claims.get("username");
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
        return expiredDate(token).after(new Date());
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

        Object rolesObject = claims.get("roles");
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
