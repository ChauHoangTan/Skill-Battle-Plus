package com.example.authservice.service;

import com.example.authservice.dto.*;
import com.example.authservice.enums.NotificationType;
import com.example.authservice.model.*;
import com.example.authservice.producer.NotificationProducer;
import com.example.authservice.repository.AuthRepository;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.utils.JWTUtils;
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

    final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final final AuthRepository authRepository;
    private final final RoleRepository roleRepository;
    private final final AuthenticationManager authenticationManager;
    private final final JWTUtils jwtUtils;
    private final final WebClient wcUserService;
    private final final RefreshTokenService refreshTokenService;
    private final final NotificationProducer notificationProducer;
    private final final OtpService otpService;

    @Autowired
    public AuthService(AuthRepository authRepository,
                       RoleRepository roleRepository,
                       AuthenticationManager authenticationManager,
                       JWTUtils jwtUtils,
                       RefreshTokenService refreshTokenService,
                       NotificationProducer notificationProducer,
                       OtpService otpService) {
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        wcUserService = WebClient.builder().baseUrl("http://localhost:8081/users").build();
        this.refreshTokenService = refreshTokenService;
        this.notificationProducer = notificationProducer;
        this.otpService = otpService;
    }

    public ResponseEntity<Object> login(LoginRequest authUserRequest, String deviceName) {
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

                refreshTokenService.createRefreshToken(refreshToken, deviceName, user.get());

                return new ResponseEntity<>(
                        new LoginResponse(accessToken, refreshToken),
                        HttpStatus.OK
                );
            }

            logger.warn("Account can not authorize!");
            return new ResponseEntity<>("Your account is not authorize!", HttpStatus.UNAUTHORIZED);

        } catch (BadCredentialsException e) {
            logger.error("Authenticate account Login error! Username: " + authUserRequest.getUsername(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> register(RegisterRequest registerRequest,
                                           BindingResult bindingResult) {
        logger.info("Register starting on account: {}", registerRequest.getUsername());
        try {
            // validate fields
            if (bindingResult.hasErrors()) {
                logger.warn("Validation failed! {}", bindingResult.getAllErrors());
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
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> requestPasswordReset(String email) {
        try {
            logger.info("Start request password reset! {}", email);

            String otpGenerated = otpService.generateOtp();
            otpService.saveOtp(email, otpGenerated);


            NotificationMessage notificationMessage = new NotificationMessage(
                    null,
                    email,
                    "Skill Battle Plus OTP",
                    "Your OPT Code is: " + otpGenerated,
                    NotificationType.EMAIL.name()
            );
            notificationProducer.sendEmailNotification(notificationMessage);


            logger.info("Request reset password succeed!");
            return new ResponseEntity<>(
                    "OTP was sent to your email!",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Error request reset password!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> verifyOtp(String email,String otp) {
        try {
            logger.info("Starting verify OTP: {}", otp);
            if(otpService.validateOtp(email, otp)) {
                logger.info("Verify OTP Succeed! {}", otp);
                otpService.deleteOtp(email);
                return new ResponseEntity<>(
                        "Verified OTP",
                        HttpStatus.OK
                );
            }
            logger.warn("OTP invalid! {}", otp);
            return new ResponseEntity<>(
                    "OTP invalid!",
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            logger.error("Verify OTP Error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> resendOtp(String email) {
        return requestPasswordReset(email);
    }

    public ResponseEntity<String> resetPassword(String email, String newPassword) {
        logger.info("Starting Reset Password! {}", email);
        try {
            Optional<AuthUser> user = authRepository.findByEmail(email);
            if(user.isEmpty()) {
                logger.warn("Email is invalid! {}", email);
                return new ResponseEntity<>(
                        "Email is invalid!",
                        HttpStatus.BAD_REQUEST
                );
            }

            user.get().setPasswordHash(JWTUtils.passwordEncoder().encode(newPassword));
            authRepository.save(user.get());
            logger.info("Reset Password Succeed!");

            return new ResponseEntity<>(
                "Reset Password Succeed!",
                HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Server is error!");
            return new ResponseEntity<>(
                    "Server is error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private final boolean createUserProfile(AuthUser authUser, RegisterRequest registerRequest) {
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

    private final NotificationMessage getNotificationMessage(RegisterRequest registerRequest) {
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
                NotificationType.EMAIL.name()
        );
    }
}
