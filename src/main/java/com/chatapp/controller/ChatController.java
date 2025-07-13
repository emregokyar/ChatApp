package com.chatapp.controller;

import com.chatapp.dto.MessageDto;
import com.chatapp.dto.UserDto;
import com.chatapp.entity.Contact;
import com.chatapp.entity.Message;
import com.chatapp.entity.User;
import com.chatapp.service.ContactService;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ChatController {
    private final UserService userService;
    private final MessageService messageService;
    private final ContactService contactService;

    @Autowired
    public ChatController(UserService userService, MessageService messageService, ContactService contactService) {
        this.userService = userService;
        this.messageService = messageService;
        this.contactService = contactService;
    }

    @GetMapping(value = "/getMessages/{channelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable("channelId") int channelId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.badRequest().build();
        List<MessageDto> messageDtoList = new ArrayList<>();
        Optional<List<Message>> messages = messageService.getMessages(channelId);
        messages.ifPresent(messageList -> {
            messageList.forEach(message -> {
                Optional<Contact> contact = contactService.getContact(currentUser.getId(), message.getSender().getId());
                String nickname;
                if (message.getSender() == currentUser) {
                    nickname = "You";
                } else {
                    nickname = contact.isPresent() ? contact.get().getNickname() : message.getSender().getUsername();
                }
                UserDto user = UserDto.builder()
                        .userId(message.getSender().getId())
                        .profilePhotoPath(message.getSender().getProfilePhotoPath())
                        .username(message.getSender().getUsername())
                        .nickname(nickname)
                        .build();

                messageDtoList.add(MessageDto.builder()
                        .content(message.getContent())
                        .id(message.getId())
                        .userDto(user)
                        .date(messageService.getMessageDateAsString(message))
                        .messageType(message.getType().toString())
                        .build());
            });
        });

        return ResponseEntity.ok(messageDtoList);
    }
}
