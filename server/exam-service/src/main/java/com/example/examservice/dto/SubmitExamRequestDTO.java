package com.example.examservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitExamRequestDTO {
    private UUID examId;
    private UUID userId;
    private List<SubmitQuizAnswerDTO> quizResults;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
}
