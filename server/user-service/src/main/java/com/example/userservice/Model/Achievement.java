package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Achievement {
    @Id
    @GeneratedValue
    private UUID id;

    private String code; // examples: FIRST_QUIZ, WIN_50_PVP...
    private String title; // examples: First Quiz Completed...
    private String description; // examples:
    private String iconUrl; // icon to show on UI

    @ManyToOne
    @JoinColumn(name = "category_id")
    private AchievementCategory category;
}
