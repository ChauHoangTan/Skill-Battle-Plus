package com.example.authService.Utils;

import com.example.authService.Model.Roles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

@Component
public class JWTUtils {
    final SecretKey secretKey = Keys.hmacShaKeyFor("chauhoangtanchauhoangtan13081308".getBytes());
    final long expireTime = 1000 * 60 * 60;

    public String generateJwtToken(String userId, String username, Set<Roles> roles) {
        return Jwts
                .builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("role", roles)
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }

    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
