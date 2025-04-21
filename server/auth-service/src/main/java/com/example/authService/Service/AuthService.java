package com.example.authService.Service;

import com.example.authService.DTO.*;
import com.example.authService.Enum.NotificationType;
import com.example.authService.Model.*;
import com.example.authService.Producer.NotificationProducer;
import com.example.authService.Repository.AuthRepository;
import com.example.authService.Repository.RoleRepository;
import com.example.authService.Utils.JWTUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class AuthService {

    final Logger logger = (Logger) LoggerFactory.getLogger(AuthService.class);
    final private AuthRepository authRepository;
    final private RoleRepository roleRepository;
    final private AuthenticationManager authenticationManager;
    final private JWTUtils jwtUtils;
    final private WebClient wcUserService;
    final private RefreshTokenService refreshTokenService;
    final private NotificationProducer notificationProducer;

    @Autowired
    public AuthService(AuthRepository authRepository,
                       RoleRepository roleRepository,
                       AuthenticationManager authenticationManager,
                       JWTUtils jwtUtils,
                       RefreshTokenService refreshTokenService,
                       NotificationProducer notificationProducer) {
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        wcUserService = WebClient.builder().baseUrl("http://localhost:8081/users").build();
        this.refreshTokenService = refreshTokenService;
        this.notificationProducer = notificationProducer;
    }

    public ResponseEntity<?> login(LoginRequest authUserRequest, String deviceName) {
        logger.info("Start authenticate login account: {}", authUserRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authUserRequest.getUsername(),
                            authUserRequest.getPassword()
                    )
            );

            Optional<AuthUser> user = authRepository.findByUsername(authUserRequest.getUsername());

            if(authentication.isAuthenticated() && user.isPresent()) {
                logger.info("Authenticated account: {}", authUserRequest.getUsername());
                logger.info("User: {}", user.get());
                logger.info("Roles: {}",  user.get().getRoles());

                String userId = String.valueOf(user.get().getId());
                String username = authUserRequest.getUsername();
                List<String> roles = user.get().getRoles().stream()
                        .map(Roles::getRole)
                        .toList();

                String accessToken = jwtUtils.generateAccessToken(
                        userId,
                        username,
                        roles
                );
                String refreshToken = jwtUtils.generateRefreshToken(
                        userId,
                        username,
                        roles
                );

                try {
                    refreshTokenService.createRefreshToken(refreshToken, deviceName, user.get());
                } catch (Exception e) {
                    logger.error("Authenticate account Login error! Username: " + authUserRequest.getUsername(), e);
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity<>(
                        new LoginResponse(accessToken, refreshToken),
                        HttpStatus.OK
                );
            }

            logger.warn("Account can not authorize!");
            return new ResponseEntity<>("Your account is not authorize!", HttpStatus.UNAUTHORIZED);

        } catch (BadCredentialsException e) {
            logger.error("Authenticate account Login error! Username: " + authUserRequest.getUsername(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> register(RegisterRequest registerRequest,
                                           BindingResult bindingResult) {
        logger.info("Register starting on account: {}", registerRequest.getUsername());
        try {
            // validate fields
            if (bindingResult.hasErrors()) {
                logger.warn("Validation failed! {}", String.valueOf(bindingResult.getAllErrors()));
                return new ResponseEntity<>(
                        String.valueOf(bindingResult.getAllErrors()),
                        HttpStatus.BAD_REQUEST
                );
            }

            // check username is existed
            Optional<AuthUser> userByUsername = authRepository.findByUsername(registerRequest.getUsername());
            if(userByUsername.isPresent()) {
                logger.warn("Username existed, please type another username! {}", registerRequest.getUsername());
                return new ResponseEntity<>(
                        "Username existed, please type another username!",
                        HttpStatus.BAD_REQUEST
                );
            }

            // check email is existed
            Optional<AuthUser> userByEmail = authRepository.findByEmail(registerRequest.getEmail());
            if(userByEmail.isPresent()) {
                logger.warn("Email existed, please type another email! {}", registerRequest.getEmail());
                return new ResponseEntity<>(
                        "Email existed, please type another email!",
                        HttpStatus.BAD_REQUEST
                );
            }

            // create authUser
            AuthUser authUser = new AuthUser();
            authUser.setUsername(registerRequest.getUsername());
            authUser.setPasswordHash(JWTUtils.passwordEncoder().encode(registerRequest.getPassword()));
            authUser.setEmail(registerRequest.getEmail());
            authUser.setEnable(true);

            Roles userRole = roleRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            authUser.setRoles(Set.of(userRole));

            authRepository.save(authUser);

            // cal UserService to create User Profile....
            if(!createUserProfile(authUser,registerRequest)) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Register failed at profile creation. Rolled back account.");
            }

            // Call event send email to Notification Service via RabbitMQ
            NotificationMessage notificationMessage = getNotificationMessage(registerRequest);
            notificationProducer.sendEmailNotification(notificationMessage);

            logger.info("Register account success! {}", registerRequest);
            return new ResponseEntity<>(
                    "Register account successfully!",
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Register error on account username: {}", registerRequest.getUsername(), e);
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private boolean createUserProfile(AuthUser authUser, RegisterRequest registerRequest) {
        try {
            UserProfileDTO userProfileDTO = new UserProfileDTO(
                    authUser.getId(),
                    registerRequest.getFullname(),
                    registerRequest.getEmail());

            this.wcUserService
                    .post()
                    .uri("/profile")
                    .bodyValue(userProfileDTO)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            return true;
        } catch (Exception e) {
            logger.error("Failed to create profile. Rolling back user: {}", authUser.getUsername(), e);
            authRepository.deleteById(authUser.getId());

            return false;
        }
    }

    private NotificationMessage getNotificationMessage(RegisterRequest registerRequest) {
        String content = """
            Hi! %s,
    
            Welcome to SKILL BATTLE PLUS! 🎉
    
            We're excited to have you on board. Get ready to challenge your skills and climb the leaderboard!
    
            👉 Start your first challenge now: https://skillbattleplus.com
    
            If you have any questions, feel free to contact our support team.
    
            Cheers,
            The Skill Battle Plus Team
            """.formatted(registerRequest.getFullname());

        return new NotificationMessage(
                null,
                registerRequest.getEmail(),
                "Welcome to SKILL BATTLE PLUS!",
                content,
                NotificationType.email.name()
        );
    }
}
