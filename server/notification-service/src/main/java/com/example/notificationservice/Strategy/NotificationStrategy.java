package com.example.notificationservice.Strategy;

import com.example.notificationservice.DTO.NotificationMessage;

public interface NotificationStrategy {
    public void sendNotification(NotificationMessage message);
}
