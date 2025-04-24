package com.example.userservice.Repository;

import com.example.userservice.Model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSettings, UUID> {
}
