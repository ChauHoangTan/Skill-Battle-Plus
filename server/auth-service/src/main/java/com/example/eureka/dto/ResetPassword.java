package com.example.eureka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPassword {
    private String email;
    private String otp;
    private String newPassword;
}
