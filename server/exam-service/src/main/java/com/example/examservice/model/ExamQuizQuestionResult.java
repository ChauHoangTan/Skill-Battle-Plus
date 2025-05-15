package com.example.examservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuizQuestionResult {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_quiz_result_id")
    private ExamQuizResult examQuizResult;

    private UUID questionId;

    private String userAnswer;

    private String correctAnswer;

    private Boolean isCorrect;

    private int score;
}
