package com.chatapp.repository;

import com.chatapp.entity.Call;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallRepository extends JpaRepository<Call, Integer> {
}
