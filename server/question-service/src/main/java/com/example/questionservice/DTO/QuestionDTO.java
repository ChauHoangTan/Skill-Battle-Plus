package com.example.questionservice.DTO;

import com.example.questionservice.Enum.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private UUID id;
    private UUID quizId;
    private String content;
    private QuestionType questionType;

    private List<AnswerOptionDTO> options;
}
