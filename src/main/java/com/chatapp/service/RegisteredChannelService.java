package com.chatapp.service;

import com.chatapp.entity.RegisteredChannel;
import com.chatapp.entity.User;
import com.chatapp.repository.RegisteredChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegisteredChannelService {
    private final RegisteredChannelRepository registeredChannelRepository;

    @Autowired
    public RegisteredChannelService(RegisteredChannelRepository registeredChannelRepository) {
        this.registeredChannelRepository = registeredChannelRepository;
    }

    public Optional<List<RegisteredChannel>> getRegisteredChannelsByIdDesc(User user) {
        return registeredChannelRepository.getRegisteredChannelsOrderByIdDesc(user.getId());
    }

    public RegisteredChannel register(RegisteredChannel channel) {
        return registeredChannelRepository.save(channel);
    }
}
