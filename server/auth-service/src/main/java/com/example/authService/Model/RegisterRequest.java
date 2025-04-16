package com.example.authService.Model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username can not be blank!")
    private String username;

    @NotBlank(message = "Full name can not be blank!")
    private String fullName;

    @NotBlank(message = "Password can not be blank!")
    @Size(min = 10, message = "Password have to larger than 10 characters!")
    private String password;

    @Email(message = "Email is not valid!")
    private String email;
}
