package com.chatapp.repository;

import com.chatapp.entity.RegisteredChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegisteredChannelRepository extends JpaRepository<RegisteredChannel, Integer> {
    @Query(value = "SELECT rgstrcnl.*" +
            " FROM registered_channels rgstrcnl" +
            " JOIN users usr ON usr.id = rgstrcnl.user_id" +
            " JOIN channels cnl ON rgstrcnl.channel_id = cnl.id " +
            " WHERE rgstrcnl.user_id = :userId" +
            " ORDER BY cnl.updated_at DESC", nativeQuery = true)
    Optional<List<RegisteredChannel>> getRegisteredChannelsOrderByUpdateDateDesc(@Param("userId") Integer userId);
}
