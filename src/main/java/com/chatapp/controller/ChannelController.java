package com.chatapp.controller;

import com.chatapp.dto.ChannelDto;
import com.chatapp.dto.UserDto;
import com.chatapp.entity.Channel;
import com.chatapp.entity.Contact;
import com.chatapp.entity.RegisteredChannel;
import com.chatapp.entity.User;
import com.chatapp.service.*;
import com.chatapp.util.ChannelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class ChannelController {
    private final UserService userService;
    private final RegisteredChannelService registeredChannelService;
    private final ContactService contactService;
    private final ChannelService channelService;
    private final GroupRoleService groupRoleService;

    @Autowired
    public ChannelController(UserService userService, RegisteredChannelService registeredChannelService, ContactService contactService, ChannelService channelService, GroupRoleService groupRoleService) {
        this.userService = userService;
        this.registeredChannelService = registeredChannelService;
        this.contactService = contactService;
        this.channelService = channelService;
        this.groupRoleService = groupRoleService;
    }

    @GetMapping(value = "/getChannels", produces = MediaType.APPLICATION_JSON_VALUE) //Retrieves all the chats
    public ResponseEntity<List<ChannelDto>> getChats(Principal principal) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.badRequest().build();

        List<ChannelDto> channelDtoList = new ArrayList<>();
        Optional<List<RegisteredChannel>> registeredChannels = registeredChannelService.getRegisteredChannelsByIdDesc(currentUser);
        registeredChannels.ifPresent(registeredChannelList ->
                registeredChannelList.forEach((registeredChannel) -> {
                    Channel channel = registeredChannel.getChannel();
                    channelDtoList.add(
                            createChannelDto(channel, currentUser)
                    );
                }));
        return ResponseEntity.ok(channelDtoList);
    }

    @GetMapping(value = "/getChannelInfo/{channelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelDto> getChannelInfo(@PathVariable("channelId") int channelId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.badRequest().build();
        Channel channel = channelService.getChannelById(channelId);
        if (channel == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(createChannelDto(
                channel,
                currentUser)
        );
    }

    @GetMapping(value = "/getPrivateChannel/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelDto> getChannelBetweenTwoUsers(@PathVariable("contactId") int contactId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.badRequest().build();
        Optional<Channel> privateChannel = channelService.getChannelInContacts(currentUser.getId(), contactId);
        return privateChannel.map(channel ->
                        ResponseEntity.ok(createChannelDto(channel, currentUser)))
                .orElseGet(() -> ResponseEntity.ok(null));
    }

    @GetMapping(value = "/createNewPrivateChannel/{targetUserId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelDto> createNewPrivateChannel(@PathVariable("targetUserId") int targetUserId) {
        User currentUser = userService.getCurrentUser();
        Optional<User> otherUser = userService.findByUserId(targetUserId);
        Optional<Channel> privateChannel = channelService.getChannelInContacts(currentUser.getId(), targetUserId);
        if (currentUser == null || otherUser.isEmpty() || privateChannel.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        // Create a new private channel
        Channel newChannel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();
        Channel savedChannel = channelService.createChannel(newChannel);

        // Register the channels for both users
        RegisteredChannel currentUserRegisteredChannel = RegisteredChannel.builder()
                .role(groupRoleService.getRegularRole())
                .user(currentUser)
                .channel(savedChannel)
                .build();
        registeredChannelService.register(currentUserRegisteredChannel);

        RegisteredChannel otherUserRegisteredChannel = RegisteredChannel.builder()
                .role(groupRoleService.getRegularRole())
                .user(otherUser.get())
                .channel(savedChannel)
                .build();
        registeredChannelService.register(otherUserRegisteredChannel);

        return ResponseEntity.ok(createChannelDto(savedChannel, currentUser));
    }

    public ChannelDto createChannelDto(Channel channel, User currentUser) {
        ChannelDto newChannelDto = null;

        if (Objects.equals(channel.getType(), ChannelType.PRIVATE)) {
            Optional<User> otherUser = userService.getChannelUser(currentUser.getId(), channel.getId());
            if (otherUser.isPresent()) {
                Optional<Contact> contact = contactService.getContact(currentUser.getId(), otherUser.get().getId());

                String channelName = contact.isPresent() ? contact.get().getNickname() : otherUser.get().getUsername();
                String lastMessage = (channel.getMessages() != null && !channel.getMessages().isEmpty())
                        ? channel.getMessages().get(channel.getMessages().size() - 1).getContent()
                        : null;

                newChannelDto = ChannelDto.builder()
                        .date(channelService.getLastUpdatedAsString(channel))
                        .channelId(channel.getId())
                        .channelType(channel.getType())
                        .users(null)
                        .lastMessage(lastMessage)
                        .channelName(channelName)
                        .photoDir(otherUser.get().getProfilePhotoPath())
                        .build();
            }
        } else {
            Optional<List<User>> channelGroupMembers = userService.getChannelGroupMembers(currentUser.getId(), channel.getId());
            List<UserDto> userDtos = new ArrayList<>();
            channelGroupMembers.ifPresent((members) -> {
                members.forEach(user -> {
                    String nickname = contactService.getContact(currentUser.getId(), user.getId()).isPresent() ?
                            contactService.getContact(currentUser.getId(), user.getId()).get().getNickname() : user.getUsername();
                    userDtos.add(
                            UserDto.builder()
                                    .username(user.getUsername())
                                    .profilePhotoPath(user.getProfilePhotoPath())
                                    .userId(user.getId())
                                    .nickname(nickname)
                                    .build()
                    );
                });
            });
            newChannelDto = ChannelDto.builder()
                    .date(channelService.getLastUpdatedAsString(channel))
                    .photoDir(channel.getGroupPhotoPath())
                    .channelName(channel.getSubject())
                    .channelId(channel.getId())
                    .lastMessage(!channel.getMessages().isEmpty() ?
                            channel.getMessages().get(channel.getMessages().size() - 1).getContent() : null)
                    .users(userDtos)
                    .channelType(ChannelType.GROUP)
                    .build();
        }

        return newChannelDto;
    }
}
