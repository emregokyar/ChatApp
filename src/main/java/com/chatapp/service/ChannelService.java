package com.chatapp.service;

import com.chatapp.entity.Channel;
import com.chatapp.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    public String getLastUpdatedAsString(Channel channel) {
        String date;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDateTime localDateTime = channel.getUpdatedAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime yesterday = now.minusDays(1).toLocalDate().atStartOfDay();

        if (localDateTime.isAfter(startOfToday)) {
            date = localDateTime.format(timeFormatter);
        } else if (localDateTime.isAfter(yesterday) && localDateTime.isBefore(startOfToday)) {
            date = "Yesterday";
        } else {
            date = localDateTime.format(dateFormatter);
        }
        return date;
    }

    public Channel getChannelById(int channelId) {
        return channelRepository.findById(channelId).orElseThrow(() ->
                new RuntimeException("Can not find a channel associated with this id: " + channelId));
    }
}
