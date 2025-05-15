package com.example.questionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCsvDTO {
    private String id;
    private String content;
    private String questionType;
    private String visibility;
    private String options; // dạng: "A:true|B:false"
    private String tags;    // dạng: "math|basic"
}

