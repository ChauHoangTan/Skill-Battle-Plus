package com.example.quizservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionResult {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_result_id")
    private QuizResult quizResult;

    private UUID questionId;

    private String userAnswer;

    private String correctAnswer;

    private Boolean isCorrect;

    private int score;
}
