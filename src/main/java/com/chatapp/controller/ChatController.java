package com.chatapp.controller;

import com.chatapp.dto.MessageDto;
import com.chatapp.dto.UserDto;
import com.chatapp.entity.Channel;
import com.chatapp.entity.Contact;
import com.chatapp.entity.Message;
import com.chatapp.entity.User;
import com.chatapp.service.*;
import com.chatapp.util.FileUploadUtil;
import com.chatapp.util.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
public class ChatController {
    private final UserService userService;
    private final MessageService messageService;
    private final ContactService contactService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChannelService channelService;

    @Autowired
    public ChatController(UserService userService, MessageService messageService, ContactService contactService, SimpMessagingTemplate messagingTemplate, ChannelService channelService) {
        this.userService = userService;
        this.messageService = messageService;
        this.contactService = contactService;
        this.messagingTemplate = messagingTemplate;
        this.channelService = channelService;
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
                        .resourceDir(message.getFilePath())
                        .date(messageService.getMessageDateAsString(message))
                        .messageType(message.getType().toString())
                        .build());
            });
        });

        return ResponseEntity.ok(messageDtoList);
    }

    //Sending Message to an existing channel
    @MessageMapping("/send")
    public ResponseEntity<Boolean> sendMessage(@Payload MessageDto messageDto, Principal principal) {

        Optional<User> currentUser = userService.findByEmailOrPhone(principal.getName());
        Channel channel = channelService.getChannelById(messageDto.getChannelId());
        if (currentUser.isEmpty() || channel == null) return ResponseEntity.badRequest().build();
        channelService.updateChannel(channel);

        MessageType messageType;
        switch (messageDto.getMessageType()) {
            case "TEXT" -> messageType = MessageType.TEXT;
            case "PHOTO" -> messageType = MessageType.PHOTO;
            case "AUDIO" -> messageType = MessageType.AUDIO;
            default -> messageType = MessageType.FILE;
        }

        Message newMessage = Message.builder()
                .type(messageType)
                .sender(currentUser.get())
                .createdAt(new Date(System.currentTimeMillis()))
                .content(messageDto.getContent())
                .channel(channel)
                .build();

        // Save and send all the users
        Message savedMessage = messageService.createNewMessage(newMessage);
        if (savedMessage == null) return ResponseEntity.badRequest().build();

        MessageDto newMessageDto = null;
        for (var receiverId : messageDto.getReceivers()) {
            String nickname;
            Optional<Contact> contact = contactService.getContact(receiverId, currentUser.get().getId());
            if (contact.isPresent()) nickname = contact.get().getNickname();
            else nickname = currentUser.get().getUsername();

            newMessageDto = MessageDto.builder()
                    .id(savedMessage.getId())
                    .date(messageService.getMessageDateAsString(savedMessage))
                    .messageType(messageType.toString())
                    .content(savedMessage.getContent())
                    .resourceDir(savedMessage.getFilePath())
                    .channelId(channel.getId())
                    .userDto(UserDto.builder()
                            .profilePhotoPath(currentUser.get().getProfilePhotoPath())
                            .userId(currentUser.get().getId())
                            .nickname(nickname)
                            .username(currentUser.get().getUsername())
                            .build())
                    .build();

            messagingTemplate.convertAndSendToUser(
                    receiverId.toString(), "/messages", newMessageDto
            );
        }

        // Also sending back the message yourself, so I can use the message id to save the data
        if (messageType != MessageType.TEXT && newMessageDto != null) {
            messagingTemplate.convertAndSendToUser(
                    currentUser.get().getId().toString(), "/sendBack", newMessageDto
            );
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping(value = "/saveFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDto> saveFile(@RequestParam("file") MultipartFile multipartFile,
                                               @RequestParam("channelId") Integer channelId,
                                               @RequestParam("messageId") Integer messageId) {
        User currentUser = userService.getCurrentUser();
        Channel channel = channelService.getChannelById(channelId);
        Optional<Message> message = messageService.findById(messageId);
        if (currentUser == null || Objects.equals(multipartFile.getOriginalFilename(), "") || message.isEmpty() || channel == null) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDir = "files/channels/" + channelId + "/" + message.get().getId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (IOException e) {
            throw new RuntimeException("Can not upload the file: " + e);
        }

        MessageDto newMessageDto = MessageDto.builder()
                .channelId(message.get().getChannel().getId())
                .messageType(message.get().getType().toString())
                .id(message.get().getId())
                .resourceDir(message.get().getFilePath())
                .date(messageService.getMessageDateAsString(message.get()))
                .build();
        return ResponseEntity.ok(newMessageDto);
    }
}
