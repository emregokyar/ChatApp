package com.chatapp.repository;

import com.chatapp.entity.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Integer> {
    Optional<LoginToken> findByToken(String token);

    @Query(value = "DELETE FROM login_tokens tkn" +
            " WHERE tkn.expiration_date <= :now;", nativeQuery = true)
    void deleteExpiredTokens(@Param("now") Date now);
}