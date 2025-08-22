package com.example.quizservice.dto;

import com.example.quizservice.Difficulty;
import com.example.quizservice.enums.Visibility;
import com.example.quizservice.model.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private UUID id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private List<UUID> questions;
    private List<Tag> tags;
}
