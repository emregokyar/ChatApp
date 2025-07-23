package com.chatapp.controller;

import com.chatapp.dto.CallDto;
import com.chatapp.entity.Call;
import com.chatapp.entity.Channel;
import com.chatapp.entity.Contact;
import com.chatapp.entity.User;
import com.chatapp.service.CallService;
import com.chatapp.service.ChannelService;
import com.chatapp.service.ContactService;
import com.chatapp.service.UserService;
import com.chatapp.util.CallTypes;
import com.chatapp.util.ChannelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class CallController {
    private final CallService callService;
    private final UserService userService;
    private final ChannelService channelService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ContactService contactService;

    @Autowired
    public CallController(CallService callService, UserService userService, ChannelService channelService, SimpMessagingTemplate messagingTemplate, ContactService contactService) {
        this.callService = callService;
        this.userService = userService;
        this.channelService = channelService;
        this.messagingTemplate = messagingTemplate;
        this.contactService = contactService;
    }

    @MessageMapping("/makeCall")
    public void makeCall(@Payload CallDto callDto, Principal principal) {
        Optional<User> currentUser = userService.findByEmailOrPhone(principal.getName());
        Channel currentChannel = channelService.getChannelById(callDto.getChannelId());
        if (currentUser.isEmpty() || currentChannel == null) return;

        channelService.updateChannel(currentChannel); // Updating the channel

        CallTypes type = (callDto.getCallType().equals("VIDEO")) ? CallTypes.VIDEO : CallTypes.VOICE;
        Call newCall = Call.builder()
                .caller(currentUser.get())
                .channel(currentChannel)
                .type(type)
                .build();
        Call savedCall = callService.saveCall(newCall);

        if (currentChannel.getType() == ChannelType.GROUP) {

            Optional<List<User>> usersInChannel = userService.getUsersInChannel(currentUser.get().getId(), currentChannel.getId());
            usersInChannel.ifPresent(users -> users.forEach(
                    user -> {
                        Integer userId = user.getId();
                        if (!Objects.equals(userId, currentUser.get().getId())) {
                            CallDto newCallDto = CallDto.builder()
                                    .callId(savedCall.getId())
                                    .channelId(currentChannel.getId())
                                    .callerDisplayName(currentChannel.getSubject())
                                    .callerPhotoPath(currentChannel.getGroupPhotoPath())
                                    .build();
                            messagingTemplate.convertAndSendToUser(
                                    userId.toString(), "/calls", newCallDto
                            );
                        }
                    }
            ));
        } else {
            Optional<User> channelUser = userService.getChannelUser(currentUser.get().getId(), currentChannel.getId());
            if (channelUser.isPresent()) {
                Integer userId = channelUser.get().getId();
                if (!Objects.equals(userId, currentUser.get().getId())) {
                    Optional<Contact> contact = contactService.getContact(userId, currentUser.get().getId());
                    Optional<User> foundUser = userService.findByUserId(userId);
                    String photoDir = null;
                    String displayName = null;
                    if (foundUser.isPresent()) {
                        photoDir = currentUser.get().getProfilePhotoPath();
                        displayName = contact.isPresent() ? contact.get().getNickname() : currentUser.get().getUsername();
                    }

                    CallDto newCallDto = CallDto.builder()
                            .callId(savedCall.getId())
                            .callerDisplayName(displayName)
                            .callerPhotoPath(photoDir)
                            .channelId(currentChannel.getId())
                            .build();
                    messagingTemplate.convertAndSendToUser(
                            userId.toString(), "/calls", newCallDto
                    );
                }
            }
        }
    }
}
