package com.example.userservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendProfileDTO {
    private UUID friendId;
    private UUID userId;
    private String name;
    private String avatar;
    private LocalDateTime beFriendAt;
}
