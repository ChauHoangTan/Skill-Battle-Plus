package com.example.examservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuiz {
    @Id
    @GeneratedValue
    private UUID id;

    private Exam exam;
    private UUID quizId;

    private int orderInExam;
    private int timeLimitInMinutes;
}
