package com.chatapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallDto {
    private Integer callId;
    private Integer channelId;
    private String callerDisplayName;
    private String callerPhotoPath;
    private String callType;
}
