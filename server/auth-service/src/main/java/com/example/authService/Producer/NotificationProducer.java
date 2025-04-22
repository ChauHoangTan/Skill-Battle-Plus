package com.example.authService.Producer;

import com.example.authService.DTO.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NotificationProducer {
    final private Logger logger = LoggerFactory.getLogger(NotificationProducer.class);
    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";
    public static final String ROUTING_KEY_EMAIL = "notify.email";
    public static final String ROUTING_KEY_PUSH = "notify.push";

    final private RabbitTemplate rabbitTemplate;

    @Autowired
    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmailNotification(NotificationMessage message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(
                    NOTIFICATION_EXCHANGE,
                    ROUTING_KEY_EMAIL,
                    jsonMessage
            );
        } catch (Exception e) {
            logger.error("Send email notification register is error!", e);
        }

    }
}
