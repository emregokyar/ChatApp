package com.chatapp.repository;

import com.chatapp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query(value = "SELECT DISTINCT msg.*" +
            " FROM messages msg" +
            " WHERE msg.channel_id = :channelId" +
            " ORDER BY msg.created_at;", nativeQuery = true)
    Optional<List<Message>> findMessagesAtChannel(@Param("channelId") Integer channelId);
}
