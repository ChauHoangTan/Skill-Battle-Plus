package com.example.quizservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID quizId;

    private Integer score;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @OneToMany(mappedBy = "quizResult", fetch = FetchType.EAGER)
    private List<QuizQuestionResult> questionResults;

    // getter/setter
}
