package com.example.battleservice.service;

import com.example.battleservice.dto.*;
import com.example.battleservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleService {
    private final SimpMessagingTemplate messageTemplate;
    private final StringRedisTemplate redisTemplate;
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8083/questions").build();
    private static final String GET_QUESTIONS_URI = "/api/questions/";

    public void submitAnswer(AnswerMessageDTO answer) {
        // If question already answered by user, ignore
        String userAnsweredKey = "battle:room:" + answer.getRoomId() + ":user:" + answer.getUserId() + ":questions";
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userAnsweredKey, answer.getQuestionId()))) {
            log.info("User {} already answered question {} in room {}", answer.getUserId(), answer.getQuestionId(), answer.getRoomId());
            return;
        }

        String roomKey = "battle:room:" + answer.getRoomId() + ":answers";
        String correctAnswer = (String) redisTemplate.opsForHash().get(roomKey, answer.getQuestionId());

        boolean isCorrect = correctAnswer != null && correctAnswer.equals(answer.getAnswer());
        int score = isCorrect ? 10 : 0;

        ScoreMessageDTO scoreMessage = new ScoreMessageDTO();
        scoreMessage.setRoomId(answer.getRoomId());
        scoreMessage.setScores(Map.of(answer.getUserId(), score));

        // Cache user's score in Redis, check if not exist then create new
        String userScoreKey = "battle:room:" + answer.getRoomId() + ":scores";
        redisTemplate.opsForHash().increment(userScoreKey, answer.getUserId(), score);
        redisTemplate.expire(userScoreKey, 1, TimeUnit.HOURS);

        // Cache user's answered questions to prevent multiple submissions
        redisTemplate.opsForSet().add(userAnsweredKey, answer.getQuestionId());
        redisTemplate.expire(userAnsweredKey, 1, TimeUnit.HOURS);

        messageTemplate.convertAndSend("/topic/room." + answer.getRoomId(), scoreMessage);
    }

    public void startBattle(StartBattleMessageDTO message, String userId, String roles, String username) {
        // Get questions from Question Service
        List<QuestionDTO> questions = new ArrayList<>();

        String roomKey = "battle:room:" + message.getRoomId() + ":answers";
        ApiResponse<List<QuestionDTO>> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_QUESTIONS_URI)
                        .queryParam("limit", 5)
                        .build())
                .header("X-userId", String.valueOf(userId))
                .header("X-roles", roles)
                .header("X-username", username)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<QuestionDTO>>>() {})
                .block();

        // Cache quiz's questions to Redis
        Map<String, String> answers = new HashMap<>();
        if (response != null && response.getData() != null) {
            for (QuestionDTO question : response.getData()) {
                answers.put(question.getId().toString(), String.valueOf(question.getOptions().stream()
                        .filter(AnswerOptionDTO::getIsCorrect)
                        .map(option -> option.getId().toString())
                        .findFirst()));

                // Add to list questions send to client
                questions.add(question);
            }
        }

        redisTemplate.opsForHash().putAll(roomKey, answers);
        redisTemplate.expire(roomKey, 1, TimeUnit.HOURS);

        // Notify users in the room that the battle has started
        questions.forEach(q -> q.setOptions(null));
        message.setQuestions(questions);
        messageTemplate.convertAndSend("/topic/room." + message.getRoomId(), message);

        log.info("Battle started in room: {} by user: {}", message.getRoomId(), userId);
    }

    public void endBattle(String roomId) {
        String roomKey = "battle:room:" + roomId + ":answers";
        redisTemplate.delete(roomKey);

        // Rank users by score
        String userScoreKey = "battle:room:" + roomId + ":scores";
        Map<Object, Object> scoreMap = redisTemplate.opsForHash().entries(userScoreKey);
        List<ScoreDTO> rankings = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : scoreMap.entrySet()) {
            rankings.add(new ScoreDTO((String) entry.getKey(), ((Number) entry.getValue()).intValue()));
        }
        // Sort rankings in descending order
        rankings.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        EndBattleMessageDTO endMessage = new EndBattleMessageDTO();
        endMessage.setRoomId(roomId);
        endMessage.setRankings(rankings);
        messageTemplate.convertAndSend("/topic/room." + roomId, endMessage);

        log.info("Battle ended in room: {}", roomId);
    }
}
