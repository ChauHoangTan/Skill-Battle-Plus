package com.example.battleservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NotNull(message = "Fields can not null")
public class AnswerOptionDTO {

    private UUID id;
    private String text;
    private Boolean isCorrect;
}
