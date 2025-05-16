package com.example.questionservice.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResultDTO {
    private UUID questionId;

    private List<UUID> userAnswer;

    private List<UUID> correctAnswer;

    private Boolean isCorrect;

    private int score;
}
