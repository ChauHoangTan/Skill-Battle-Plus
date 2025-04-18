package com.example.authService.Service;

import com.example.authService.Model.AuthUser;
import com.example.authService.Model.RefreshToken;
import com.example.authService.Repository.RefreshTokenRepository;
import com.example.authService.Utils.JWTUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenService {

    final private RefreshTokenRepository refreshTokenRepository;
    final private Logger logger = (Logger) LoggerFactory.getLogger(RefreshTokenService.class);
    final private JWTUtils jwtUtils;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JWTUtils jwtUtils) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtils = jwtUtils;
    }

    public void createRefreshToken(String refreshToken, String deviceName, AuthUser authUser) {
        try {
            logger.info("Create Refresh Token! {}", refreshToken);

            RefreshToken newRefreshToken = new RefreshToken();
            newRefreshToken.setToken(refreshToken);
            newRefreshToken.setDeviceName(deviceName);
            newRefreshToken.setAuthUser(authUser);

            RefreshToken storedToken = refreshTokenRepository.save(newRefreshToken);

            if (storedToken.getId() != null) {
                logger.info("Refresh Token Created! {}", refreshToken);
                return;
            }

            logger.warn("Refresh Token Cannot Create! {}", refreshToken);
        } catch (Exception e) {
            logger.error("Refresh Token Cannot Create! {}", e.getMessage());
        }
    }

    public ResponseEntity<String> refreshAccessToken(String refreshToken) {
        try {
            logger.info("Start refreshing access token! {}", refreshToken);
            Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findByToken(refreshToken);

            if(storedRefreshToken.isEmpty()) {
                logger.warn("Refresh Token is invalid!");
                return new ResponseEntity<>(
                        "Refresh Token is invalid!",
                        HttpStatus.BAD_REQUEST
                );
            }

            if(!jwtUtils.validateToken(refreshToken)) {
                refreshTokenRepository.delete(storedRefreshToken.get());

                logger.warn("Refresh Token is expired!");
                return new ResponseEntity<>(
                        "Refresh Token is expired!",
                        HttpStatus.BAD_REQUEST
                );
            }

            String accessToken = jwtUtils.generateAccessToken(
                    jwtUtils.extractId(refreshToken),
                    jwtUtils.extractUsername(refreshToken),
                    jwtUtils.getRoles(refreshToken)
            );
            logger.info("Refresh access token successfully! {}", accessToken);
            return new ResponseEntity<>(
                    accessToken,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Refresh Token is invalid! {}", e.getMessage());
            return new ResponseEntity<>(
                    "Server is error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }
}
