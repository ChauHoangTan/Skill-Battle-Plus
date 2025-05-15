package com.example.notificationservice.consumer;

import com.example.notificationservice.config.NotificationConfig;
import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = {NotificationConfig.EMAIL_QUEUE, NotificationConfig.PUSH_QUEUE})
    public void sendEmail(String message) throws JsonProcessingException {
        NotificationMessage notificationMessage = objectMapper.readValue(message, NotificationMessage.class);
        notificationService.sendMessage(notificationMessage);
    }

}
