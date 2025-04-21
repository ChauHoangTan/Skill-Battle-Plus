package com.example.notificationservice.Strategy;

import com.example.notificationservice.DTO.NotificationMessage;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationStrategy implements NotificationStrategy{
    @Override
    public void sendNotification(NotificationMessage message) {

    }
}
