package com.example.notificationservice.strategy;

import com.example.notificationservice.dto.NotificationMessage;

public interface NotificationStrategy {
    public void sendNotification(NotificationMessage message);
}
