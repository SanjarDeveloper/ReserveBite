package com.example.reservebite.repository;

import com.example.reservebite.entity.Message;
import com.example.reservebite.entity.Users;
import com.example.reservebite.entity.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndRecipientOrRecipientAndSenderOrderByCreatedAtAsc(
            Users user1, Users user2, Users user3, Users user4);

    List<Message> findByRecipientAndStatusOrderByCreatedAtDesc(Users recipient, MessageStatus status);

    @Modifying
    @Query("UPDATE Message m SET m.status = com.example.reservebite.entity.enums.MessageStatus.READ " +
            "WHERE m.recipient = :user AND m.status = com.example.reservebite.entity.enums.MessageStatus.UNREAD")
    void markMessagesAsRead(@Param("user") Users user);

    long countByRecipientAndStatus(Users recipient, MessageStatus status);

    List<Message> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.recipient = :recipient ORDER BY m.sender.username")
    List<Users> findDistinctSendersByRecipient(@Param("recipient") Users recipient);
}