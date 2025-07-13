package com.chatapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {
    private Integer id;
    private String content;
    private UserDto userDto;
    private String date;
    private String messageType;
    private String resourceDir;
}
