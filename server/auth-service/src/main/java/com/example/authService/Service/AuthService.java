package com.example.authService.Service;

import com.example.authService.Model.AuthUser;
import com.example.authService.Model.AuthUserRequest;
import com.example.authService.Model.RegisterRequest;
import com.example.authService.Repository.AuthRepository;
import com.example.authService.Utils.JWTUtils;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.util.Logger;

import java.util.Optional;

@Service
public class AuthService {

    final private AuthRepository authRepository;
    final private AuthenticationManager authenticationManager;
    final private JWTUtils jwtUtils;
    final Logger logger = (Logger) LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(AuthRepository authRepository,
                       AuthenticationManager authenticationManager,
                       JWTUtils jwtUtils) {
        this.authRepository = authRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<String> login(AuthUserRequest authUserRequest) {
        logger.info("Start authenticate login account: ", authUserRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authUserRequest.getUsername(),
                            authUserRequest.getPassword()
                    )
            );

            Optional<AuthUser> user = authRepository.findByUsername(authUserRequest.getUsername());

            if(authentication.isAuthenticated() && user.isPresent()) {
                logger.info("Authenticated account: ", authUserRequest.getUsername());
                return new ResponseEntity<>(
                        jwtUtils.generateJwtToken(
                                String.valueOf(user.get().getId()),
                                authUserRequest.getUsername(),
                                user.get().getRoles()
                        ),
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

    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest,
                                           BindingResult bindingResult) {
        logger.info("Register starting on account: ", registerRequest.getUsername());
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(
                        String.valueOf(bindingResult.getAllErrors()),
                        HttpStatus.BAD_REQUEST
                );
            }

            AuthUser authUser = new AuthUser();
            authUser.setUsername(registerRequest.getUsername());
            authUser.setPasswordHash(JWTUtils.passwordEncoder().encode(registerRequest.getPassword()));
            authUser.setEmail(registerRequest.getEmail());
            authUser.setEnable(true);
            authRepository.save(authUser);

            // cal UserService to create User Profile....

            return new ResponseEntity<>(
                    "Register account successfully!",
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Register error on account username: " + registerRequest.getUsername(),
                    e.getMessage(), e);
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
