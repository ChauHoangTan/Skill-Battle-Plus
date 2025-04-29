package com.example.questionservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerOptionDTO {
    private UUID id;
    private String text;
    private Boolean isCorrect;
}
