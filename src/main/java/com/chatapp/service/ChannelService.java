package com.chatapp.service;

import com.chatapp.entity.Channel;
import com.chatapp.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public Channel createChannel(Channel channel) {
        channel.setUpdatedAt(new Date(System.currentTimeMillis()));
        return channelRepository.save(channel);
    }
}
