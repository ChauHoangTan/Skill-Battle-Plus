package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.factory.NotificationStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationStrategyFactory notificationStrategyFactory;
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    public NotificationService(NotificationStrategyFactory notificationStrategyFactory) {
        this.notificationStrategyFactory = notificationStrategyFactory;
    }

    public void sendMessage(NotificationMessage message) {
        try {
            logger.info("Starting send message.... {}", message);
            notificationStrategyFactory.getStrategy(message.getType()).sendNotification(message);
            logger.info("Sent message successfully!");
        } catch (Exception e) {
            logger.error("Error while sending message! {}", message);
        }

    }
}
