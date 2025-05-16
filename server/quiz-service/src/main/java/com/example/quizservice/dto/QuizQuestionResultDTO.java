package com.example.quizservice.dto;

import com.example.quizservice.dto.QuizResultDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionResultDTO {
    private UUID questionId;

    private List<UUID> userAnswer;

    private List<UUID> correctAnswer;

    private Boolean isCorrect;

    private int score;
}
