package com.example.notificationservice.Strategy;

import com.example.notificationservice.DTO.NotificationMessage;
import com.example.notificationservice.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationStrategy implements NotificationStrategy{
    final private EmailService emailService;

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
