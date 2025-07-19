package com.chatapp.repository;

import com.chatapp.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {

    @Query(value = "SELECT DISTINCT cnl.*" +
            " FROM channels cnl" +
            " JOIN registered_channels rgstr1 ON cnl.id = rgstr1.channel_id" +
            " JOIN registered_channels rgstr2 ON cnl.id = rgstr2.channel_id" +
            " WHERE cnl.type = 'PRIVATE'" +
            " AND ((rgstr1.user_id = :contacterId AND rgstr2.user_id = :contactingId)" +
            " OR (rgstr1.user_id = :contactingId AND rgstr2.user_id = :contacterId));", nativeQuery = true)
    Optional<Channel> findByContacterAndContacting(@Param("contacterId") Integer contacterId, @Param("contactingId") Integer contactingId);
}