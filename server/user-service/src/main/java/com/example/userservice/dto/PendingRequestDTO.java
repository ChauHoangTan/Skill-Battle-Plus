package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingRequestDTO {
    private UUID requestId;
    private UUID fromUserId;
    private String fromFullName;
    private String fromAvatarUrl;
    private LocalDateTime sentAt;
}
