package com.example.questionservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ListQuestionDTO {
    private List<QuestionDTO> questionDTOList;
}
