package com.example.userservice.Controller;

import com.example.userservice.DTO.UserFriendDTO;
import com.example.userservice.Model.UserProfile;
import com.example.userservice.Service.UserFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/users/friends")
public class UserFriendController {

    final private UserFriendService userFriendService;

    @Autowired
    public UserFriendController(UserFriendService userFriendService) {
        this.userFriendService = userFriendService;
    }

    @GetMapping("")
    public ResponseEntity<?> getFriends(@RequestHeader("X-userId") UUID userId) {
        return userFriendService.getFriends(userId);
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getPendingRequests(@RequestHeader("X-userId") UUID userId) {
        return userFriendService.getPendingRequests(userId);
    }

    @PostMapping("/requests")
    public ResponseEntity<String> sendRequest(@RequestHeader("X-userId") UUID sender,@RequestBody UserFriendDTO receiver) {
        return userFriendService.sendRequest(sender, receiver.getId());
    }

    @PatchMapping("/requests/{requestId}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable UUID requestId) {
        return userFriendService.rejectRequest(requestId);
    }

    @PatchMapping("/requests/{requestId}/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable UUID requestId) {
        return userFriendService.acceptRequest(requestId);
    }

    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<String> cancelRequest(@PathVariable UUID requestId) {
        return userFriendService.cancelRequest(requestId);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable UUID friendId) {
        return userFriendService.removeFriend(friendId);
    }

}
