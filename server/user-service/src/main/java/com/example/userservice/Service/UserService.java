package com.example.userservice.Service;

import com.example.userservice.DTO.UpdateUserProfileDTO;
import com.example.userservice.Model.UserProfile;
import com.example.userservice.DTO.UserProfileDTO;
import com.example.userservice.Model.UserSettings;
import com.example.userservice.Repository.UserProfileRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    final private UserProfileRepository userProfileRepository;
    final private FileStorageService fileStorageService;
    final private UserSettingService userSettingService;
    final Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);

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

        Optional<UserSettings> userSettings = userSettingService.createUserSettings(userProfileDTO.getId());

        if(savedUserProfile.getId() != null && userSettings.isPresent()) {
            logger.info("Create User Profile Successfully! {}", userProfile);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }

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
            try {
                contentType = Files.probeContentType(resource.getFile().toPath());
            } catch (IOException ignored) {}
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Object> getProfile(UUID userId) {
        try {
            ModelMapper mapper = new ModelMapper();
            Optional<UserProfile> userProfile = userProfileRepository.findById(userId);

            if(userProfile.isEmpty()) {
                return new ResponseEntity<>(
                        "User ID is not exist!",
                        HttpStatus.BAD_REQUEST
                );
            }

            UserProfileDTO userProfileDTO = mapper.map(userProfile, UserProfileDTO.class);

            return new ResponseEntity<>(
                    userProfileDTO,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<String> updateProfile(UUID userId, UpdateUserProfileDTO profile) {
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

        return new ResponseEntity<>(
                "Update User Profile Succeed!",
                HttpStatus.OK
        );
    }
}
