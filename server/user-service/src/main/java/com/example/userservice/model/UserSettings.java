package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSettings {

    @Id
    private UUID userId;

    private String preferredLanguage = "en";
    private boolean darkModeEnable = false;
    private boolean notificationEnable = true;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    private UserProfile userProfile;

    public UserSettings(UUID userId) {
        this.userId = userId;
    }
}
