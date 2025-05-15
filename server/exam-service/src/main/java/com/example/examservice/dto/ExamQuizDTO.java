package com.example.examservice.dto;

import com.example.examservice.model.Exam;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuizDTO {
    @Id
    @GeneratedValue
    private UUID id;

    private Exam exam;
    private UUID quizId;

    private int orderInExam;
    private int timeLimitInMinutes;
}
