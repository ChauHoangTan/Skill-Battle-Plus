package com.example.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username can not be blank!")
    private String username;

    @NotBlank(message = "Full name can not be blank!")
    private String fullname;

    @NotBlank(message = "Password can not be blank!")
    @Size(min = 6, message = "Password have to larger than 10 characters!")
    private String password;

    @Email(message = "Email is not valid!")
    private String email;
}
