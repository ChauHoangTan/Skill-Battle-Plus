package com.example.userservice.service;

import com.example.userservice.dto.UserSettingDTO;
import com.example.userservice.model.UserProfile;
import com.example.userservice.model.UserSettings;
import com.example.userservice.repository.UserSettingRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserSettingService {

    private final UserSettingRepository userSettingRepository;
    private final Logger logger = LoggerFactory.getLogger(UserSettingService.class);
    private final ModelMapper mapper = new ModelMapper();

    public UserSettingService(UserSettingRepository userSettingRepository) {
        this.userSettingRepository = userSettingRepository;
    }

    public Optional<UserSettings> createUserSettings(UserProfile userProfile) {
        UserSettings userSettings = new UserSettings();
        userSettings.setUserProfile(userProfile);
        return Optional.of(userSettingRepository.save(userSettings));
    }

    public void deleteUserSettings(UUID userId) {
        userSettingRepository.deleteById(userId);
    }

    public ResponseEntity<Object> getUserSettings(UUID userId) {
        try {
            Optional<UserSettings> userSettings = userSettingRepository.findById(userId);

            if(userSettings.isEmpty()) {
                return new ResponseEntity<>(
                        "User ID is not found!",
                        HttpStatus.OK
                );
            }

            UserSettingDTO userSettingDTO = mapper.map(userSettings.get(), UserSettingDTO.class);
            return new ResponseEntity<>(
                    userSettingDTO,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> createSettings(UUID userId, UserSettingDTO userSettingDTO) {
        logger.info("Creating User Settings... {}", userId);
        try {
            UserSettings userSettings = new UserSettings(userId);
            userSettings.setPreferredLanguage(userSettingDTO.getPreferredLanguage());
            userSettings.setNotificationEnable(userSettingDTO.isNotificationEnable());
            userSettings.setDarkModeEnable(userSettingDTO.isDarkModeEnable());

            UserSettings storedUserSettings = userSettingRepository.save(userSettings);

            if(!storedUserSettings.getUserId().equals(userId)) {
                logger.error("ERROR When Creating User Settings! {}", userId);
                return new ResponseEntity<>(
                        "ERROR When Creating User Settings!",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            logger.info("Create User Settings Succeed! {}", userId);
            return new ResponseEntity<>(
                    "Create User Settings Succeed!",
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Error Create User Profile!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> updateSettings(UUID userId, UserSettingDTO userSettingDTO) {
        logger.info("Creating User Settings... {}", userId);
        logger.info("User Setting DTO: {}", userSettingDTO);
        try {
            UserSettings userSettings = new UserSettings(userId);
            userSettings.setPreferredLanguage(userSettingDTO.getPreferredLanguage());
            userSettings.setDarkModeEnable(userSettingDTO.isDarkModeEnable());
            userSettings.setNotificationEnable(userSettingDTO.isNotificationEnable());

            UserSettings storedUserSettings = userSettingRepository.save(userSettings);

            if(!storedUserSettings.getUserId().equals(userId)) {
                logger.error("ERROR When Updating User Settings! {}", userId);
                return new ResponseEntity<>(
                        "ERROR When Updating User Settings!",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            logger.info("Update User Settings Succeed! {}", userId);
            return new ResponseEntity<>(
                    "Update User Settings Succeed!",
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Error Update User Profile!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> resetDefault(UUID userId) {
        try {
            UserSettings userSettings = new UserSettings(userId);
            UserSettings storedUserSettings = userSettingRepository.save(userSettings);
            if(!storedUserSettings.getUserId().equals(userId)) {
                logger.error("Reset Settings Error!");
                return new ResponseEntity<>(
                        "Server Error!",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            return new ResponseEntity<>(
                    "Reset User Settings Succeed!",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Reset Settings Error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
