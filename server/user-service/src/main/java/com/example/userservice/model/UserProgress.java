package com.example.userservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProgress {

    @Id
    private UUID progressId;

    @ManyToOne
    @JoinColumn(name = "userProfileId", nullable = false)
    private UserProfile userProfile;

    private UUID quizId;
    private ProgressStatus status;
    private LocalDateTime lastDid;
}
