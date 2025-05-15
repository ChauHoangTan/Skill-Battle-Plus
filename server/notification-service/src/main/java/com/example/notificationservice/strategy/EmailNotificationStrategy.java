package com.example.notificationservice.strategy;

import com.example.notificationservice.dto.NotificationMessage;
import com.example.notificationservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationStrategy implements NotificationStrategy{
    private final EmailService emailService;

    @Autowired
    public EmailNotificationStrategy(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendNotification(NotificationMessage message) {
        emailService.sendEmail(message.getUserEmail(),
                message.getSubject(),
                message.getContent());
    }
}
