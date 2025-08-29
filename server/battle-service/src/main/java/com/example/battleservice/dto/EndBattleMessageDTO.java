package com.example.battleservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndBattleMessageDTO {
    private String roomId;
    private String quizId;
    private List<ScoreDTO> rankings;
}
