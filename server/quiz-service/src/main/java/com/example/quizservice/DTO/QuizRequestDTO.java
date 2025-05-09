package com.example.quizservice.DTO;

import com.example.quizservice.Difficulty;
import com.example.quizservice.Enum.Visibility;
import com.example.quizservice.Model.Tag;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuizRequestDTO {
    private UUID id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private Visibility visibility;
    private List<Tag> tags;
    private List<UUID> oldQuestions;
    private List<QuestionDTO> newQuestions;
}
