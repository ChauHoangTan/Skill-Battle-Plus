package com.example.questionservice.producer;

import com.example.questionservice.config.MessageQueueConfig;
import com.example.questionservice.dto.QuestionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendQuestionMessage(String routingKey, QuestionDTO message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(
                    MessageQueueConfig.QUESTION_EXCHANGE,
                    routingKey,
                    jsonMessage
            );
            log.info("Sent message to routing key {}: {}", routingKey, message);
        } catch (Exception e) {
            log.error("Failed to send message to routing key {}: {}", routingKey, e.getMessage(), e);
        }
    }

}
