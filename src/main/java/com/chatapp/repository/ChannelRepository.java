package com.chatapp.repository;

import com.chatapp.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {
}
