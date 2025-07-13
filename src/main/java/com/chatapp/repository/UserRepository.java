package com.chatapp.repository;

import com.chatapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT DISTINCT usr.*" +
            " FROM users usr" +
            " JOIN registered_channels rgstr ON rgstr.user_id = usr.id" +
            " JOIN channels cnl ON rgstr.channel_id = cnl.id" +
            " WHERE rgstr.channel_id = :channelId" +
            " AND rgstr.user_id != :userId" +
            " AND cnl.type = 'GROUP'", nativeQuery = true)
    Optional<List<User>> getGroupMembers(@Param("userId") Integer userId, @Param("channelId") Integer channelId);

    @Query(value = "SELECT DISTINCT usr.*" +
            " FROM users usr" +
            " JOIN registered_channels rgstr ON rgstr.user_id = usr.id" +
            " JOIN channels cnl ON rgstr.channel_id = cnl.id" +
            " WHERE rgstr.channel_id = :channelId" +
            " AND rgstr.user_id != :userId" +
            " AND cnl.type = 'PRIVATE'", nativeQuery = true)
    Optional<User> getSingleUser(@Param("userId") Integer userId, @Param("channelId") Integer channelId);
}
