package com.chatapp.dto;

import com.chatapp.util.ChannelType;
import lombok.*;

import java.util.List;

@Data
@Builder
public class ChannelDto {
    private Integer channelId;
    private ChannelType channelType;
    private String photoDir;
    private String channelName;
    private List<UserDto> users;
    private String lastMessage;
    private String date;
}
