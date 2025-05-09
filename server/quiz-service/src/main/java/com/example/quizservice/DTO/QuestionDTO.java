package com.example.quizservice.DTO;

import com.example.quizservice.Enum.QuestionType;
import com.example.quizservice.Enum.Visibility;
import com.example.quizservice.Model.Tag;
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

    private List<Tag> tags;
    private List<AnswerOptionDTO> options;
}
