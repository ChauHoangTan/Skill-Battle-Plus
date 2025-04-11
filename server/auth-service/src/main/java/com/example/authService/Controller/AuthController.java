package com.example.authService.Controller;

import com.example.authService.Model.AuthUserRequest;
import com.example.authService.Model.RegisterRequest;
import com.example.authService.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AuthController {
    final private AuthService authService;

    @Autowired
    public AuthController(AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthUserRequest authUserRequest) {
        return authService.login(authUserRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest,
                                           BindingResult bindingResult) {
        return authService.register(registerRequest, bindingResult);
    }
}
