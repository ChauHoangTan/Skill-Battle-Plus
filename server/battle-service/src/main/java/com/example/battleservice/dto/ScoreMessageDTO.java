package com.example.battleservice.dto;
import lombok.Data;

import java.util.Map;

@Data
public class ScoreMessageDTO {
    private String roomId;
    private Map<String, Integer> scores;
}
