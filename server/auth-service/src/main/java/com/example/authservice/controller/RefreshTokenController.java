package com.example.authservice.controller;

import com.example.authservice.dto.RefreshTokenRequest;
import com.example.authservice.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/tokens")
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refresh(@RequestBody RefreshTokenRequest refreshToken) {
        return this.refreshTokenService.refreshAccessToken(refreshToken.getRefreshToken());
    }
}
