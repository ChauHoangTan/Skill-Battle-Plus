package com.example.examservice.dto;

import com.example.examservice.enums.ExamMode;
import com.example.examservice.enums.Visibility;
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
public class ExamDTO {
    private UUID id;

    private String title;
    private String description;

    private int totalScore;

    @Enumerated(EnumType.STRING)
    private ExamMode mode;

    private List<ExamQuizDTO> examQuizzes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int duration;

    private Visibility visibility;
}
