package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementCategory {
    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private com.example.userservice.enums.AchievementCategory category;
}
