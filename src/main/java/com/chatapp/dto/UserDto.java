package com.chatapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Integer userId;
    private String username;
    private String profilePhotoPath;
    private String nickname;
}
