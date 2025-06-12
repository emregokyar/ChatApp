package com.chatapp.service;

import com.chatapp.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }
}
