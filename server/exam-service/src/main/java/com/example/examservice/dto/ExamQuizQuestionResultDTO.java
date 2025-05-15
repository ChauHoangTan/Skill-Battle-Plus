package com.example.examservice.dto;

import com.example.examservice.model.ExamQuizResult;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuizQuestionResultDTO {
    private ExamQuizResult examQuizResult;

    private UUID questionId;

    private String userAnswer;

    private String correctAnswer;

    private Boolean isCorrect;

    private int score;
}
