package com.example.quizservice.dto;

import com.example.examservice.model.QuizResultDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionResultDTO {
    private QuizResultDTO quizResult;

    private UUID questionId;

    private String userAnswer;

    private String correctAnswer;

    private Boolean isCorrect;

    private int score;
}
