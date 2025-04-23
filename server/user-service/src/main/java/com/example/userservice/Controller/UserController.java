package com.example.userservice.Controller;

import com.example.userservice.DTO.UpdateUserProfileDTO;
import com.example.userservice.DTO.UserProfileDTO;
import com.example.userservice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    final private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(@RequestHeader("X-userId") UUID userId) {
        return userService.getProfile(userId);
    }

    @PostMapping("/profile")
    public ResponseEntity<Boolean> createProfile(@RequestBody UserProfileDTO userProfile) {
        return userService.createProfile(userProfile);
    }

//    @PutMapping("/profile")
//    public ResponseEntity<String> updateProfile(@RequestHeader("X-userId") UUID userId,
//                                                UpdateUserProfileDTO updateUserProfileDTO) {
//        return userService.updateProfile(userId, updateUserProfileDTO);
//    }

    @PutMapping("/profile/avatar")
    public ResponseEntity<String> updateAvatar(@RequestHeader("X-userId") UUID userId,
                                               @RequestParam MultipartFile file) {
        return userService.updateAvatar(userId, file);
    }

    @GetMapping("/api/users/profile/avatar/{filename:.+}")
    public ResponseEntity<Resource> serveAvatar(@PathVariable String filename) {
        return userService.serveAvatar(filename);
    }
}
