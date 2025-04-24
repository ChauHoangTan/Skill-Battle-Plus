package com.example.userservice.Controller;

import com.example.userservice.DTO.UserSettingDTO;
import com.example.userservice.Service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/settings")
public class UserSettingController {

    final private UserSettingService userSettingService;

    @Autowired
    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @GetMapping("")
    public ResponseEntity<?> getUserSettings(@RequestHeader("X-userId") UUID userId) {
        return userSettingService.getUserSettings(userId);
    }

    @PostMapping("")
    public ResponseEntity<String> createSettings(@RequestHeader("X-userId" ) UUID userId, UserSettingDTO userSettingDTO) {
        return userSettingService.createSettings(userId, userSettingDTO);
    }

    @PutMapping("")
    public ResponseEntity<String> updateSettings(@RequestHeader("X-userId") UUID userId, UserSettingDTO userSettingDTO) {
        return userSettingService.updateSettings(userId, userSettingDTO);
    }

    @DeleteMapping("")
    public ResponseEntity<String> resetDefault(@RequestHeader("X-userId") UUID userId) {
        return userSettingService.resetDefault(userId);
    }
}
