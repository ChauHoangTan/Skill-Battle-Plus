package com.example.userservice.repository;

import com.example.userservice.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSettings, UUID> {
}
