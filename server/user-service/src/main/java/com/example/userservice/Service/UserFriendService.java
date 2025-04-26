package com.example.userservice.Service;

import com.example.userservice.Enum.FriendRequestStatus;
import com.example.userservice.Model.Friend;
import com.example.userservice.Model.FriendRequest;
import com.example.userservice.Model.UserProfile;
import com.example.userservice.Repository.UserFriendRepository;
import com.example.userservice.Repository.UserFriendRequestRepository;
import com.example.userservice.Repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserFriendService {

    final private Logger logger = LoggerFactory.getLogger(UserFriendService.class);
    final private UserProfileRepository userProfileRepository;
    final private UserFriendRepository userFriendRepository;
    final private UserFriendRequestRepository userFriendRequestRepository;

    @Autowired
    public UserFriendService(UserProfileRepository userProfileRepository,
                            UserFriendRepository userFriendRepository,
                            UserFriendRequestRepository userFriendRequestRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userFriendRepository = userFriendRepository;
        this.userFriendRequestRepository = userFriendRequestRepository;
    }

    public ResponseEntity<?> getFriends(UUID userId) {
        logger.info("Get list friends of user {}", userId);
        try {
            Optional<UserProfile> user = userProfileRepository.findById(userId);

            if(user.isEmpty()) {
                logger.error("User ID is not found! {}", userId);
                return new ResponseEntity<>(
                        "User ID is not found!",
                        HttpStatus.BAD_REQUEST
                );
            }

            Set<Friend> listFriendSentRequest = user.get().getFriendsSentRequests();
            Set<Friend> listFriendReceivedRequest = user.get().getFriendsReceivedRequests();

            Set<UserProfile> friends = new HashSet<>();
            listFriendSentRequest.forEach(friend -> friends.add(friend.getFriendsReceived()));
            listFriendReceivedRequest.forEach(friend -> friends.add(friend.getFriendsSent()));

            logger.info("Get friends succeed!");
            return new ResponseEntity<>(
                    friends,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Get Friends Error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<?> getPendingRequests(UUID userId) {
        logger.info("Get pending requests! {}", userId);
        try {
            Optional<UserProfile> user = userProfileRepository.findById(userId);

            if(user.isEmpty()) {
                logger.error("User ID is not found! {}", userId);
                return new ResponseEntity<>(
                        "User ID is not found!",
                        HttpStatus.BAD_REQUEST
                );
            }

            Set<FriendRequest> userRequests = userFriendRequestRepository.findFriendRequestByReceiverId(userId);
            Set<UserProfile> userProfiles = new HashSet<>();
            userRequests.forEach(
                    userRequest -> userProfiles.add(userRequest.getSender())
            );

            logger.info("Get request succeed! {}", userId);
            return new ResponseEntity<>(
                    userProfiles,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            logger.error("Get Friends Error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> sendRequest(UUID sender, UUID receiver) {
        logger.info("Starting send request! {}", sender);
        try {
            Optional<UserProfile> senderProfile = userProfileRepository.findById(sender);
            Optional<UserProfile> receiverProfile = userProfileRepository.findById(receiver);

            if(senderProfile.isEmpty() || receiverProfile.isEmpty()) {
                logger.error("User ID is not found!");
                return new ResponseEntity<>(
                        "User ID is not found!",
                        HttpStatus.BAD_REQUEST
                );
            }

            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(senderProfile.get());
            friendRequest.setReceiver(receiverProfile.get());
            friendRequest.setStatus(FriendRequestStatus.PENDING);

            FriendRequest storedRequest = userFriendRequestRepository.save(friendRequest);
            if(!storedRequest.getSender().getId().equals(sender) || !storedRequest.getReceiver().getId().equals(receiver)) {
                logger.error("Error while save friend request!");
                return new ResponseEntity<>(
                        "Server Error!",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            logger.info("Sent request succeed!");
            return new ResponseEntity<>(
                    "Sent friend request!",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Get Friends Error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> updateFriendRequestStatus(UUID requestId, FriendRequestStatus status) {
        logger.info("Starting friend request! request id: {}", requestId);
        try {
            Optional<FriendRequest> friendRequest = userFriendRequestRepository.findById(requestId);

            if (friendRequest.isEmpty()) {
                logger.error("Friend request not found!");
                return new ResponseEntity<>(
                        "Friend request not found!",
                        HttpStatus.BAD_REQUEST
                );
            }

            friendRequest.get().setStatus(status);
            userFriendRequestRepository.save(friendRequest.get());

            logger.info("Reject request succeed!");
            return new ResponseEntity<>(
                    "Request succeed!",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Friend request error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> rejectRequest(UUID requestId) {
        return updateFriendRequestStatus(requestId, FriendRequestStatus.REJECTED);
    }

    public ResponseEntity<String> acceptRequest(UUID requestId) {
        return updateFriendRequestStatus(requestId, FriendRequestStatus.ACCEPTED);
    }

    public ResponseEntity<String> cancelRequest(UUID requestId) {
        logger.info("Starting cancel request");
        try {
            Optional<FriendRequest> friendRequest = userFriendRequestRepository.findById(requestId);

            if(friendRequest.isEmpty()) {
                logger.error("Friend request not found!");
                return new ResponseEntity<>(
                        "Friend request not found!",
                        HttpStatus.BAD_REQUEST
                );
            }

            userFriendRequestRepository.deleteById(friendRequest.get().getId());
            return new ResponseEntity<>(
                    "Cancel friend request succeed!",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Cancel request error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<String> removeFriend(UUID friendId) {
        logger.info("Starting remove friend");
        try {
            Optional<Friend> friend = userFriendRepository.findById(friendId);

            if(friend.isEmpty()) {
                logger.error("Friend not found!");
                return new ResponseEntity<>(
                        "Friend not found!",
                        HttpStatus.BAD_REQUEST
                );
            }

            userFriendRepository.deleteById(friend.get().getId());
            return new ResponseEntity<>(
                    "Remove friend succeed!",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            logger.error("Remove friend error!", e);
            return new ResponseEntity<>(
                    "Server Error!",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
