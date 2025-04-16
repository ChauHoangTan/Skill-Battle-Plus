package com.example.userservice.Controller;

import com.example.userservice.Model.UserProfileDTO;
import com.example.userservice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {
    final private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/profile")
    public ResponseEntity<String> createProfile(@RequestBody UserProfileDTO userProfile) {
        return userService.createProfile(userProfile);
    }
}
