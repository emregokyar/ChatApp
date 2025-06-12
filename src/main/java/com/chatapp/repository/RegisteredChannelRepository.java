package com.chatapp.repository;

import com.chatapp.entity.RegisteredChannel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredChannelRepository extends JpaRepository<RegisteredChannel, Integer> {
}
