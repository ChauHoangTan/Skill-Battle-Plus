package com.example.userservice.Service;

import com.example.userservice.Model.UserProfile;
import com.example.userservice.Model.UserProfileDTO;
import com.example.userservice.Repository.UserProfileRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    final private UserProfileRepository userProfileRepository;
    final Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public ResponseEntity<Boolean> createProfile(UserProfileDTO userProfileDTO) {
        ModelMapper mapper = new ModelMapper();
        UserProfile userProfile = mapper.map(userProfileDTO, UserProfile.class);

        UserProfile savedUserProfile  = userProfileRepository.save(userProfile);

        if(savedUserProfile.getId() != null) {
            logger.info("Create User Profile Successfully!", userProfile);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }

        logger.error("ERROR While Create UserProfile!!", userProfile);
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }
}
