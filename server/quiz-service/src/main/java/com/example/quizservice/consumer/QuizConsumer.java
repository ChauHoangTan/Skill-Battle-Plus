package com.example.quizservice.consumer;

import com.example.quizservice.config.MessageQueueConfig;
import com.example.quizservice.document.QuizDocument;
import com.example.quizservice.dto.QuizDTO;
import com.example.quizservice.model.Tag;
import com.example.quizservice.repository.QuizSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizConsumer {
    private final QuizSearchRepository quizSearchRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final static int maxRetryCount = 5;

    @RabbitListener(queues = MessageQueueConfig.QUIZ_QUEUE)
    private void handleQuizMessage(Message message) {
        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            QuizDTO quizDTO = objectMapper.readValue(message.getBody(), QuizDTO.class);

            switch (routingKey) {
                case MessageQueueConfig.ROUTING_KEY_QUIZ_CREATE, MessageQueueConfig.ROUTING_KEY_QUIZ_UPDATE -> {
                    quizSearchRepository.save(this.mapQuizDtoToQuizDocument(quizDTO));
                }
                case MessageQueueConfig.ROUTING_KEY_QUIZ_DELETE -> {
                    quizSearchRepository.deleteById(String.valueOf(quizDTO.getId()));
                }
                default -> {
                    log.warn("Received message with unknown routing key: {}", routingKey);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleDeadLetterMessage(Message message) {
        try {
            Integer count = (Integer) message.getMessageProperties().getHeaders().getOrDefault("x-retry-count", 0);
            count++;

            log.info("Retrying message, current retry count: {}", count);
            if(count > maxRetryCount) {
                rabbitTemplate.send(
                        MessageQueueConfig.QUIZ_DLX_EXCHANGE,
                        MessageQueueConfig.ROUTING_KEY_QUIZ_FAILED,
                        message
                );
            } else {
                String originalRoutingKey = (String) message.getMessageProperties().getHeaders()
                        .getOrDefault("x-original-routing-key", message.getMessageProperties().getReceivedRoutingKey());

                rabbitTemplate.send(
                        MessageQueueConfig.QUIZ_EXCHANGE,
                        originalRoutingKey,
                        MessageBuilder.withBody(message.getBody())
                                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                                .setHeader("x-retry-count", count)
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("Failed in retry processing, sending to DLQ: {}", e.getMessage(), e);
            rabbitTemplate.send(
                    MessageQueueConfig.QUIZ_DLX_EXCHANGE,
                    MessageQueueConfig.ROUTING_KEY_QUIZ_FAILED,
                    message
            );
            throw new RuntimeException(e);
        }
    }

    private QuizDocument mapQuizDtoToQuizDocument(QuizDTO dto) {
        QuizDocument quizDocument = new QuizDocument();
        quizDocument.setId(String.valueOf(dto.getId()));
        quizDocument.setTitle(dto.getTitle());
        quizDocument.setDescription(dto.getDescription());
        quizDocument.setDifficulty(dto.getDifficulty().name());
        quizDocument.setVisibility(dto.getVisibility().name());
        quizDocument.setTags(
                dto.getTags().stream()
                        .map(Tag::getName)
                        .toList()
        );

        return quizDocument;
    }
}
