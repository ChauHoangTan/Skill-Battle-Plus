package com.example.userservice.repository;

import com.example.userservice.model.Friend;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFriendRepository extends JpaRepository<Friend, UUID> {
    @Query("SELECT fr FROM Friend fr WHERE fr.friendsSent.id = :sender AND fr.friendsReceived.id = :receiver")
    public Optional<Friend> findByFriendsSentIdAndFriendReceivedId(
            @Param("sender") UUID sender,
            @Param("receiver") UUID receiver);
}
