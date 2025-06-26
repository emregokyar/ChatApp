package com.chatapp.repository;

import com.chatapp.entity.RegisteredChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegisteredChannelRepository extends JpaRepository<RegisteredChannel, Integer> {
    @Query(value = "SELECT DISTINCT cnl.*" +
            " FROM registered_channels cnl" +
            " JOIN users usr ON usr.id = cnl.user_id" +
            " WHERE cnl.user_id = :userId" +
            " ORDER BY cnl.user_id DESC", nativeQuery = true)
    Optional<List<RegisteredChannel>> getRegisteredChannelsOrderByIdDesc(@Param("userId") Integer userId);
}
