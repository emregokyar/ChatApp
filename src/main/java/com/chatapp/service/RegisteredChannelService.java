package com.chatapp.service;

import com.chatapp.repository.RegisteredChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisteredChannelService {
    private final RegisteredChannelRepository registeredChannelRepository;

    @Autowired
    public RegisteredChannelService(RegisteredChannelRepository registeredChannelRepository) {
        this.registeredChannelRepository = registeredChannelRepository;
    }
}
