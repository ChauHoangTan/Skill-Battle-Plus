package com.example.examservice.model;

import com.example.examservice.enums.ExamMode;
import com.example.examservice.enums.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;

    private int totalScore;

    @Enumerated(EnumType.STRING)
    private ExamMode mode;

    @OneToMany(mappedBy = "exam", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ExamQuiz> examQuizzes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int duration;

    private Visibility visibility;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
