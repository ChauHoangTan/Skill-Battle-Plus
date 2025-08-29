package com.example.questionservice.consumer;

import com.example.questionservice.config.MessageQueueConfig;
import com.example.questionservice.document.AnswerOptionDocument;
import com.example.questionservice.document.QuestionDocument;
import com.example.questionservice.dto.AnswerOptionDTO;
import com.example.questionservice.dto.QuestionDTO;
import com.example.questionservice.model.Tag;
import com.example.questionservice.repository.QuestionSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionConsumer {
    private final QuestionSearchRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final static int maxRetryCount = 5;

    @RabbitListener(queues = MessageQueueConfig.QUESTION_QUEUE)
    public void handleQuestionMessage(Message message) {
        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            ObjectMapper objectMapper = new ObjectMapper();
            QuestionDTO dto = objectMapper.readValue(message.getBody(), QuestionDTO.class);

            switch (routingKey) {
                case MessageQueueConfig.ROUTING_KEY_QUESTION_CREATE, MessageQueueConfig.ROUTING_KEY_QUESTION_UPDATE -> {
                    QuestionDocument questionDocument = this.mapQuestionDtoToQuestionDocument(dto);
                    repository.save(questionDocument);
                }
                case MessageQueueConfig.ROUTING_KEY_QUESTION_DELETE -> {
                    repository.deleteById(String.valueOf(dto.getId()));
                }
                default -> {
                    log.warn("Received message with unknown routing key: {}", routingKey);
                }
            }
        } catch (Exception e) {
            log.error("Error processing message: {}, sending to retry queue", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = MessageQueueConfig.RETRY_QUEUE)
    public void handleDeadLetterMessage(Message message) {
        try {
            Integer count = (Integer) message.getMessageProperties().getHeaders().getOrDefault("x-retry-count", 0);
            count++;
            log.info("Retrying message, current retry count: {}", count);
            if(count > maxRetryCount) {
                rabbitTemplate.send(
                        MessageQueueConfig.QUESTION_DLX_EXCHANGE,
                        MessageQueueConfig.ROUTING_KEY_QUESTION_FAILED,
                        message
                );
            } else {
                String originalRoutingKey = (String) message.getMessageProperties().getHeaders()
                        .getOrDefault("x-original-routing-key", message.getMessageProperties().getReceivedRoutingKey());
                rabbitTemplate.send(
                        MessageQueueConfig.QUESTION_EXCHANGE,
                        MessageQueueConfig.QUESTION_QUEUE,
                        MessageBuilder.withBody(message.getBody())
                                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                                .setHeader("x-retry-count", count)
                                .setHeader("x-original-routing-key", originalRoutingKey)
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("Failed in retry processing, sending to DLQ: {}", e.getMessage(), e);
            rabbitTemplate.send(
                    MessageQueueConfig.QUESTION_DLX_EXCHANGE,
                    MessageQueueConfig.ROUTING_KEY_QUESTION_FAILED,
                    message
            );
            throw new RuntimeException(e);
        }
    }

    private QuestionDocument mapQuestionDtoToQuestionDocument(QuestionDTO questionDTO) {
        QuestionDocument questionDocument = new QuestionDocument();
        questionDocument.setId(String.valueOf(questionDTO.getId()));
        questionDocument.setQuizId(String.valueOf(questionDTO.getQuizId()));
        questionDocument.setContent(questionDTO.getContent());
        questionDocument.setQuestionType(questionDTO.getQuestionType().name());
        questionDocument.setVisibility(questionDTO.getVisibility().name());
        questionDocument.setTags(
                questionDTO.getTags()
                        .stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList())
        );
        questionDocument.setOptions(
                questionDTO.getOptions()
                        .stream()
                        .map(
                                option -> {
                                    return new AnswerOptionDocument(option.getText(), option.getIsCorrect());
                                }
                        )
                        .collect(Collectors.toList())
        );

        return questionDocument;
    }
}
