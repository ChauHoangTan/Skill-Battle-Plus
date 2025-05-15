package com.example.userservice.service;

import com.example.userservice.dto.FriendProfileDTO;
import com.example.userservice.dto.PendingRequestDTO;
import com.example.userservice.enums.FriendRequestStatus;
import com.example.userservice.model.Friend;
import com.example.userservice.model.FriendRequest;
import com.example.userservice.model.UserProfile;
import com.example.userservice.repository.UserFriendRepository;
import com.example.userservice.repository.UserFriendRequestRepository;
import com.example.userservice.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserFriendService {

    private final Logger logger = LoggerFactory.getLogger(UserFriendService.class);
    private final UserProfileRepository userProfileRepository;
    private final UserFriendRepository userFriendRepository;
    private final UserFriendRequestRepository userFriendRequestRepository;

    @Autowired
    public UserFriendService(UserProfileRepository userProfileRepository,
                            UserFriendRepository userFriendRepository,
                            UserFriendRequestRepository userFriendRequestRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userFriendRepository = userFriendRepository;
        this.userFriendRequestRepository = userFriendRequestRepository;
    }

    public ResponseEntity<Object> getFriends(UUID userId) {
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

            Set<FriendProfileDTO> friends = new HashSet<>();
            listFriendSentRequest.forEach(friend -> friends.add(
                    new FriendProfileDTO(
                            friend.getId(),
                            friend.getFriendsReceived().getId(),
                            friend.getFriendsReceived().getName(),
                            friend.getFriendsReceived().getAvatarURL(),
                            friend.getCreatedAt()
                    )
            ));
            listFriendReceivedRequest.forEach(friend -> friends.add(
                    new FriendProfileDTO(
                            friend.getId(),
                            friend.getFriendsSent().getId(),
                            friend.getFriendsSent().getName(),
                            friend.getFriendsSent().getAvatarURL(),
                            friend.getCreatedAt()
                    ))
            );

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

    public ResponseEntity<Object> getPendingRequests(UUID userId) {
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
            Set<PendingRequestDTO> userProfiles = new HashSet<>();
            userRequests.forEach(
                    userRequest -> userProfiles.add(
                            new PendingRequestDTO(
                                    userRequest.getId(),
                                    userRequest.getSender().getId(),
                                    userRequest.getSender().getName(),
                                    userRequest.getSender().getAvatarURL(),
                                    userRequest.getSentAt()
                            )
                    )
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

            if(status.equals(FriendRequestStatus.ACCEPTED)) {
                Friend friend = new Friend();
                friend.setFriendsSent(friendRequest.get().getSender());
                friend.setFriendsReceived(friendRequest.get().getReceiver());

                userFriendRepository.save(friend);
            }

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
            Optional<FriendRequest> friendRequest = userFriendRequestRepository.findBySenderIdAndReceiverIdAndStatus(
                        friend.get().getFriendsSent().getId(),
                        friend.get().getFriendsReceived().getId(),
                        FriendRequestStatus.ACCEPTED
                    );
            if(friendRequest.isPresent()) {
                FriendRequest changeFriendRequest = friendRequest.get();
                changeFriendRequest.setStatus(FriendRequestStatus.DELETED);
                userFriendRequestRepository.save(changeFriendRequest);
            }

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
