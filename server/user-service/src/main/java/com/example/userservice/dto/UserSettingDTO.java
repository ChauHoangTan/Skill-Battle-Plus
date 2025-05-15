package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingDTO {
    private String preferredLanguage;
    private boolean darkModeEnable;
    private boolean notificationEnable;
}
