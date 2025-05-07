package com.example.quizservice.DTO;

import com.example.quizservice.Difficulty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuizRequestDTO {
    private UUID id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private List<UUID> questions;
}
