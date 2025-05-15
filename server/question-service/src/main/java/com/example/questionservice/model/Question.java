package com.example.questionservice.model;

import com.example.questionservice.enums.QuestionType;
import com.example.questionservice.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Setter
@Getter
public class Question {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID quizId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @ManyToMany
    private List<Tag> tags;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<AnswerOption> options = new ArrayList<>();

    @Column(nullable = false)
    private UUID createdBy;
}
