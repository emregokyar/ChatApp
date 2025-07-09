package com.chatapp.repository;

import com.chatapp.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    @Query(value = "SELECT *" +
            " FROM contacts cnt" +
            " WHERE cnt.contacter_id = :userId" +
            " ORDER BY cnt.nickname", nativeQuery = true)
    Optional<List<Contact>> findAllSavedContacts(@Param("userId") Integer userId);

    @Query(value = "SELECT DISTINCT cnt.*" +
            " FROM contacts cnt" +
            " WHERE cnt.contacter_id = :userId" +
            " AND cnt.contacting_id = :contactId", nativeQuery = true)
    Optional<Contact> findContactIfExists(@Param("userId") Integer userId,  @Param("contactId") Integer contactId);
}
