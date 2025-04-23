package com.example.authService.Controller;

import com.example.authService.DTO.EmailRequest;
import com.example.authService.DTO.LoginRequest;
import com.example.authService.DTO.RegisterRequest;
import com.example.authService.DTO.ResetPassword;
import com.example.authService.Service.AuthService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    final private AuthService authService;

    @Autowired
    public AuthController(AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest authUserRequest, HttpServletRequest request) {
        return authService.login(authUserRequest, request.getHeader("User-Agent"));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest,
                                           BindingResult bindingResult) {
        return authService.register(registerRequest, bindingResult);
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody EmailRequest emailRequest) {
        return authService.requestPasswordReset(emailRequest.getEmail());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody ResetPassword resetPassword) {
        return authService.verifyOtp(resetPassword.getEmail(), resetPassword.getOtp());
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody EmailRequest emailRequest) {
        return authService.resendOtp(emailRequest.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPassword resetPassword) {
        return authService.resetPassword(resetPassword.getEmail(), resetPassword.getNewPassword());
    }
}
