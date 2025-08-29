package com.example.battleservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NotNull(message = "Fields can not null")
public class QuestionDTO {
    private UUID id;
    private UUID quizId;
    private String content;

    private List<AnswerOptionDTO> options;
}
