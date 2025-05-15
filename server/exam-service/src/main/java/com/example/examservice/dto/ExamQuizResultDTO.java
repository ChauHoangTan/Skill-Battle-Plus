package com.example.examservice.dto;

import jakarta.persistence.*;
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
public class ExamQuizResultDTO {
    private ExamResultDTO examResult;

    private UUID quizId;

    private int score;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private List<ExamQuizQuestionResultDTO> questionsResult;
}
