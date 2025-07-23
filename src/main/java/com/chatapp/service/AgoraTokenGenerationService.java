package com.chatapp.service;

import io.agora.media.RtcTokenBuilder2;
import org.springframework.stereotype.Service;

@Service
public class AgoraTokenGenerationService {
    private static final String AGORA_APP_ID = System.getenv("AGORA_APP_ID");
    private static final String AGORA_APP_CERTIFICATE = System.getenv("AGORA_APP_CERTIFICATE");
    // Token validity time in seconds !!
    private static final int TOKEN_EXPIRATION = 3600;
    // The validity time of all permissions in seconds !!
    private static final int PRIVILEGE_EXPIRATION = 3600;

    //Token generation to join channel
    public String generateToken(Integer channelId, int userId) {
        if (channelId == null || channelId <= 0 || userId <= 0) return null;
        RtcTokenBuilder2 tokenBuilder = new RtcTokenBuilder2();
        return tokenBuilder.buildTokenWithUid(
                AGORA_APP_ID,
                AGORA_APP_CERTIFICATE,
                channelId.toString(),
                userId,
                RtcTokenBuilder2.Role.ROLE_SUBSCRIBER,
                TOKEN_EXPIRATION,
                PRIVILEGE_EXPIRATION
        );
    }
}
