package com.example.userservice.controller;

import com.example.userservice.dto.UpdateUserProfileDTO;
import com.example.userservice.dto.UserProfileDTO;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users/profile")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getProfile(@RequestHeader("X-userId") UUID userId) {
        return userService.getProfile(userId);
    }

    @PostMapping("")
    public ResponseEntity<Boolean> createProfile(@RequestBody UserProfileDTO userProfile) {
        return userService.createProfile(userProfile);
    }

    @PutMapping("")
    public ResponseEntity<String> updateProfile(@RequestHeader("X-userId") UUID userId,
                                                @RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
        return userService.updateProfile(userId, updateUserProfileDTO);
    }

    @PutMapping(path = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAvatar(@RequestHeader("X-userId") UUID userId,
                                               @RequestParam MultipartFile file) {
        return userService.updateAvatar(userId, file);
    }

    @GetMapping("/avatar/{filename:.+}")
    public ResponseEntity<Resource> serveAvatar(@PathVariable String filename) {
        return userService.serveAvatar(filename);
    }
}
