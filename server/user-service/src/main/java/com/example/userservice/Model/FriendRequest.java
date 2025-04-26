package com.example.userservice.Model;

import com.example.userservice.Enum.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequest {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private UserProfile sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private UserProfile receiver;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status; // PENDING, ACCEPTED, REJECTED

    private LocalDateTime sentAt;

    @PrePersist
    private void onCreate() {
        this.sentAt = LocalDateTime.now();
    }
}
