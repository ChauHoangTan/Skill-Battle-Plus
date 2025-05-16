package com.example.quizservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {

    private UUID quizId;

    private int score;

    private LocalDateTime startAt;

    private LocalDateTime submittedAt;

    private List<QuizQuestionResultDTO> questionsResult;
}
