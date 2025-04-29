package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
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

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private Set<FriendRequest> sentRequests;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private Set<FriendRequest> receivedRequests;

    @OneToMany(mappedBy = "friendsSent", fetch = FetchType.LAZY)
    private Set<Friend> friendsSentRequests;

    @OneToMany(mappedBy = "friendsReceived", fetch = FetchType.LAZY)
    private Set<Friend> friendsReceivedRequests;

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
