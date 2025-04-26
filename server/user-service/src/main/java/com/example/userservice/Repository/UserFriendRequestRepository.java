package com.example.userservice.Repository;

import com.example.userservice.Enum.FriendRequestStatus;
import com.example.userservice.Model.Friend;
import com.example.userservice.Model.FriendRequest;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserFriendRequestRepository extends JpaRepository<FriendRequest, UUID> {

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.receiver.id = :receiverId AND fr.status = 'PENDING'")
    public Set<FriendRequest> findFriendRequestByReceiverId(@Param("receiverId") UUID receiverId);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :sender AND fr.receiver.id = :receiver AND fr.status = :status")
    public Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(
            @Param("sender") UUID sender,
            @Param("receiver") UUID receiver,
            @Param("status") FriendRequestStatus status
    );
}
