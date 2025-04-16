package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    @Id
    private UUID id;

    private String name;
    private String avatarURL;
    private String country;
    private String email;
    private LocalDate birthday;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private UserStats userStats;

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private UserSettings userSettings;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<UserProgress> userProgress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<UserFriend> userSendRequest;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<UserFriend> friendSendRequest;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
