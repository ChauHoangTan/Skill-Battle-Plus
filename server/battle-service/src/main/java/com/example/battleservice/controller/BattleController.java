package com.example.battleservice.controller;

import com.example.battleservice.dto.AnswerMessageDTO;
import com.example.battleservice.dto.StartBattleMessageDTO;
import com.example.battleservice.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
public class BattleController {
    private final BattleService battleService;

    @MessageMapping("/submitAnswer")
    public void submitAnswer(AnswerMessageDTO answer) {
        this.battleService.submitAnswer(answer);
    }

    @MessageMapping("/startBattle")
    public void startBattle(StartBattleMessageDTO message,
                            @RequestHeader("X-userId") String userId,
                            @RequestHeader("X-roles") String roles,
                            @RequestHeader("X-username") String username) {
        this.battleService.startBattle(message, userId, roles, username);
    }

    @MessageMapping("/endBattle")
    public void endBattle(String roomId) {
        this.battleService.endBattle(roomId);
    }
}
