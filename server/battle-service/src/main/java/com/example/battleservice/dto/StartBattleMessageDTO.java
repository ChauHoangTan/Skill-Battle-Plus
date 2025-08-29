package com.example.battleservice.dto;
import lombok.Data;

import java.util.List;

@Data
public class StartBattleMessageDTO {
    private String roomId;
    private String quizId;
    private List<QuestionDTO> questions;
}
