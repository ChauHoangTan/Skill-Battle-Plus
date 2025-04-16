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
public class UserStats {

    @Id
    private UUID userId;

    private long totalQuizzesTaken;
    private long totalCorrectAnswers;
    private int totalBattle;
    private int totalBattleWon;
    private double averageScore;
    private int rankingPoints;
    private int level;
    private int lastActive;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    private UserProfile userProfile;
}
