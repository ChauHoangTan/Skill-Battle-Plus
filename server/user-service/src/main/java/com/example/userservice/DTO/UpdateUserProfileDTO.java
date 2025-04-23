package com.example.userservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileDTO {
    private String name;
    private String country;
    private String email;
    private LocalDate birthday;
}
