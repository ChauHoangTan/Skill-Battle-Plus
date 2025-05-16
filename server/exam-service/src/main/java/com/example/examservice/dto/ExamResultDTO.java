package com.example.examservice.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDTO {
    private UUID examId;

    private UUID userId;

    private int totalScore;

    private int userScore;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private Boolean completed;

    private List<ExamQuizResultDTO> quizResults;
}
