package com.example.notificationservice.strategy;

import com.example.notificationservice.dto.NotificationMessage;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationStrategy implements NotificationStrategy{
    @Override
    public void sendNotification(NotificationMessage message) {
        // TODO document why this method is empty
    }
}
