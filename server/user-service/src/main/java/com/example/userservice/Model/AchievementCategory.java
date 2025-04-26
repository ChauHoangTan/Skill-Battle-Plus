package com.example.userservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

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
    private com.example.userservice.Enum.AchievementCategory category;
}
