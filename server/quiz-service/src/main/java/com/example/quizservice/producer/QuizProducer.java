package com.example.quizservice.producer;

import com.example.quizservice.config.MessageQueueConfig;
import com.example.quizservice.dto.QuizDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendQuizMessage(String routingKey, QuizDTO message) {
        try {
            log.info("Sending message to RabbitMQ with routing key {}: {}", routingKey, message);

            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(
                    MessageQueueConfig.QUIZ_EXCHANGE,
                    routingKey,
                    jsonMessage,
                    m -> {
                        m.getMessageProperties().setHeader("x-original-routing-key", routingKey);
                        return m;
                    }
            );
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ: {}", e.getMessage());
        }
    }
}
