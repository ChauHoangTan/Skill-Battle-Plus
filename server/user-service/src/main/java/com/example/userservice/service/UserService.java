package com.example.userservice.service;

import com.example.userservice.dto.UpdateUserProfileDTO;
import com.example.userservice.model.UserProfile;
import com.example.userservice.dto.UserProfileDTO;
import com.example.userservice.model.UserSettings;
import com.example.userservice.repository.UserProfileRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final FileStorageService fileStorageService;
    private final UserSettingService userSettingService;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserProfileRepository userProfileRepository,
                       FileStorageService fileStorageService,
                       UserSettingService userSettingService) {
        this.userProfileRepository = userProfileRepository;
        this.fileStorageService = fileStorageService;
        this.userSettingService = userSettingService;
    }

    public ResponseEntity<Boolean> createProfile(UserProfileDTO userProfileDTO) {
        ModelMapper mapper = new ModelMapper();
        UserProfile userProfile = mapper.map(userProfileDTO, UserProfile.class);

        UserProfile savedUserProfile  = userProfileRepository.save(userProfile);

        if(savedUserProfile.getId() != null) {
            Optional<UserSettings> userSettings = userSettingService.createUserSettings(savedUserProfile);

            if(userSettings.isEmpty()) {
                userProfileRepository.deleteById(userProfileDTO.getId());

                logger.error("ERROR While Create User Settings!! {}", userProfile);
                return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            logger.info("Create User Profile Successfully! {}", userProfile);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }

        userSettingService.deleteUserSettings(userProfileDTO.getId());
        logger.error("ERROR While Create UserProfile!! {}", userProfile);

        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> updateAvatar(UUID userId, MultipartFile file) {
        logger.info("Starting update avatar! {}", userId);
        try {
            Optional<UserProfile> user = userProfileRepository.findById(userId);
            if(user.isEmpty()) {
                logger.warn("User ID is not found! {}", userId);
                return new ResponseEntity<>(
                        "User ID is not found!",
                        HttpStatus.BAD_REQUEST
                );
            }
            String filename = fileStorageService.store(file);

//          Build image uri
            String url = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/users/profile/avatar/")
                    .path(filename)
                    .toUriString();

            user.get().setAvatarURL(url);
            userProfileRepository.save(user.get());

            logger.info("Upload avatar successfully!");

            return new ResponseEntity<>(
                    url,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Upload Avatar is error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<Resource> serveAvatar(String filename) {
        try {
            Resource resource = fileStorageService.loadAsResource(filename);
            String contentType = "application/octet-stream";

            if(Files.probeContentType(resource.getFile().toPath()) != null) {
                contentType = Files.probeContentType(resource.getFile().toPath());
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Object> getProfile(UUID userId) {
        logger.info("Starting get profile {}", userId);
        try {
            ModelMapper mapper = new ModelMapper();
            Optional<UserProfile> userProfile = userProfileRepository.findById(userId);

            if(userProfile.isEmpty()) {
                logger.warn("User ID is not exist!");
                return new ResponseEntity<>(
                        "User ID is not exist!",
                        HttpStatus.BAD_REQUEST
                );
            }

            UserProfileDTO userProfileDTO = mapper.map(userProfile.get(), UserProfileDTO.class);

            logger.info("Get profile succeed!");
            return new ResponseEntity<>(
                    userProfileDTO,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Get profile is Error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> updateProfile(UUID userId, UpdateUserProfileDTO profile) {
        logger.info("Starting Update Profile... {}", userId);
        logger.info("Update User Profile DTO: {}", profile);
        try {
            Optional<UserProfile> userProfile = userProfileRepository.findById(userId);

            if(userProfile.isEmpty()) {
                return new ResponseEntity<>(
                        "User ID is not found!",
                        HttpStatus.BAD_REQUEST
                );
            }

            UserProfile profileStored = userProfile.get();
            profileStored.setName(profile.getName());
            profileStored.setCountry(profile.getCountry());
            profileStored.setEmail(profile.getEmail());
            profileStored.setBirthday(profile.getBirthday());

            userProfileRepository.save(profileStored);

            logger.info("Update User Profile Succeed!");
            return new ResponseEntity<>(
                    "Update User Profile Succeed!",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Get profile is Error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
