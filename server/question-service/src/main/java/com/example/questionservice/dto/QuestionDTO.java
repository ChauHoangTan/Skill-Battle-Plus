package com.example.questionservice.dto;

import com.example.questionservice.enums.QuestionType;
import com.example.questionservice.enums.Visibility;
import com.example.questionservice.model.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NotNull(message = "Fields can not null")
public class QuestionDTO {
    private UUID id;
    private UUID quizId;
    private String content;
    private QuestionType questionType;
    private Visibility visibility;

    private List<AnswerOptionDTO> options;
    private List<Tag> tags;
}
