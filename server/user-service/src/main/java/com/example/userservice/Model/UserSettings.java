package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSettings {

    @Id
    private UUID userId;

    private String preferredLanguage;
    private boolean darkModeEnable;
    private boolean notificationEnable;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    private UserProfile userProfile;
}
