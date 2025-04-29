package com.example.questionservice.Model;

import jakarta.persistence.*;

import java.util.UUID;

public class AnswerOption {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Boolean isCorrect;
}
