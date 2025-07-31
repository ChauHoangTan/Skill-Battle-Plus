package com.example.eureka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationMessage {
    private String userId;
    private String userEmail;
    private String subject;
    private String content;
    private String type;
}
