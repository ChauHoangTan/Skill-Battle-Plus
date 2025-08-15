package com.example.userservice.controller;

import com.example.userservice.dto.UserSettingDTO;
import com.example.userservice.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/api/settings")
public class UserSettingController {

    private final UserSettingService userSettingService;

    @Autowired
    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getUserSettings(@RequestHeader("X-userId") UUID userId) {
        return userSettingService.getUserSettings(userId);
    }

    @PostMapping("")
    public ResponseEntity<String> createSettings(@RequestHeader("X-userId" ) UUID userId, @RequestBody UserSettingDTO userSettingDTO) {
        return userSettingService.createSettings(userId, userSettingDTO);
    }

    @PutMapping("")
    public ResponseEntity<String> updateSettings(@RequestHeader("X-userId") UUID userId, @RequestBody UserSettingDTO userSettingDTO) {
        return userSettingService.updateSettings(userId, userSettingDTO);
    }

    @DeleteMapping("")
    public ResponseEntity<String> resetDefault(@RequestHeader("X-userId") UUID userId) {
        return userSettingService.resetDefault(userId);
    }
}
