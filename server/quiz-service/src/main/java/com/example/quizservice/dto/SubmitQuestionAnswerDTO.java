package com.example.quizservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuestionAnswerDTO {
    private UUID questionId;
    private List<UUID> selectedOptionIds;
}
