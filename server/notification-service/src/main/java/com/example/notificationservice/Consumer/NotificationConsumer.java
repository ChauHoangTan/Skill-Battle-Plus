package com.example.notificationservice.Consumer;

import com.example.notificationservice.DTO.NotificationMessage;
import com.example.notificationservice.Service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    final private NotificationService notificationService;
    final private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = {"email-queue", "push-queue"})
    public void sendEmail(String message) throws JsonProcessingException {
        NotificationMessage notificationMessage = objectMapper.readValue(message, NotificationMessage.class);
        notificationService.sendMessage(notificationMessage);
    }

}
