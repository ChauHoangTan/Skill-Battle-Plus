package com.example.notificationservice.DTO;

import lombok.Data;

@Data
public class NotificationMessage {
    private String userId;
    private String userEmail;
    private String subject;
    private String content;
    private String type;
}
