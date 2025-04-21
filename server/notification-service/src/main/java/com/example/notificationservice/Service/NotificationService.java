package com.example.notificationservice.Service;

import com.example.notificationservice.DTO.NotificationMessage;
import com.example.notificationservice.Factory.NotificationStrategyFactory;
import com.example.notificationservice.Strategy.EmailNotificationStrategy;
import com.example.notificationservice.Strategy.NotificationStrategy;
import com.example.notificationservice.Strategy.PushNotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    final private NotificationStrategyFactory notificationStrategyFactory;
    final private Logger logger = LoggerFactory.getLogger(NotificationService.class);

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
