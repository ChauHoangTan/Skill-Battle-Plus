package com.example.examservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuizResult {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "exam_result_id")
    private ExamResult examResult;

    private UUID quizId;

    private Integer score;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    // getter/setter
}
