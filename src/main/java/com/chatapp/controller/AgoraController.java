package com.chatapp.controller;

import com.chatapp.entity.User;
import com.chatapp.service.AgoraTokenGenerationService;
import com.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class AgoraController {
    private final AgoraTokenGenerationService agoraTokenGenerationService;
    private final UserService userService;
    private static final String AGORA_APP_ID = System.getenv("AGORA_APP_ID");

    @Autowired
    public AgoraController(AgoraTokenGenerationService agoraTokenGenerationService, UserService userService) {
        this.agoraTokenGenerationService = agoraTokenGenerationService;
        this.userService = userService;
    }

    @GetMapping(value = "/generateAgoraToken/{channelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> generateAgoraToken(@PathVariable("channelId") Integer channelId,
                                                                  Principal principal) {
        Optional<User> currentUser = userService.findByEmailOrPhone(principal.getName());
        if (currentUser.isEmpty()) return ResponseEntity.badRequest().build();
        String newToken = agoraTokenGenerationService.generateToken(channelId, currentUser.get().getId());
        Map<String, String> response = new HashMap<>();
        response.put("token", newToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/getAppId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getAgoraAppId() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.badRequest().build();
        Map<String, String> response = new HashMap<>();
        response.put("appId", AGORA_APP_ID);
        return ResponseEntity.ok(response);
    }
}