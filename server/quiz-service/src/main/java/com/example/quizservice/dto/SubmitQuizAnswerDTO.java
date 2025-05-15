package com.example.quizservice.dto;

import com.example.quizservice.dto.SubmitQuestionAnswerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuizAnswerDTO {
    private UUID quizId;
    private List<SubmitQuestionAnswerDTO> questionAnswers;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
}
