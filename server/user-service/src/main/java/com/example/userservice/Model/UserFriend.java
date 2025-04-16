package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserProfile user;

    @ManyToOne
    @JoinColumn(name = "friendId")
    private UserProfile friend;

    private FriendStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;

    @PrePersist
    private void onCreate() {
        this.requestedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.acceptedAt = LocalDateTime.now();
    }
}
