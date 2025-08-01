package com.example.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String role;

    @ManyToMany
    private Set<AuthUser> users;
}
