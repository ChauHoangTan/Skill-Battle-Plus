package com.example.battleservice.dto;
import lombok.Data;

@Data
public class AnswerMessageDTO {
    private String roomId;
    private String userId;
    private String questionId;
    private String answer;
}
