package com.chatapp.controller;

import com.chatapp.dto.UserDto;
import com.chatapp.entity.Contact;
import com.chatapp.entity.User;
import com.chatapp.service.ContactService;
import com.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;
    private final ContactService contactService;

    @Autowired
    public UserController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

    @GetMapping(value = "/getUserInfo/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUserInfo(@PathVariable("userId") int userId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.badRequest().build();
        Optional<User> searchedUser = userService.findByUserId(userId);
        if (searchedUser.isEmpty()) return ResponseEntity.ok(null);

        Optional<Contact> contact = contactService.getContact(currentUser.getId(), searchedUser.get().getId());
        String nickname;
        if (contact.isPresent()) nickname = contact.get().getNickname();
        else nickname = searchedUser.get().getUsername();

        return searchedUser.map(user -> ResponseEntity.ok(UserDto.builder()
                        .profilePhotoPath(user.getProfilePhotoPath())
                        .userId(user.getId())
                        .username(user.getUsername())
                        .nickname(nickname)
                        .build()))
                .orElseGet(() -> ResponseEntity.ok(null));
    }

    @GetMapping(value = "/getUserArray/{channelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<int[]> getUserArray(@PathVariable("channelId") int channelId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.badRequest().build();
        Optional<List<User>> usersInChannel = userService.getUsersInChannel(currentUser.getId(), channelId);
        if (usersInChannel.isEmpty()) return ResponseEntity.ok(null);
        int[] usersArray = new int[usersInChannel.get().size()];
        for (int i = 0; i < usersInChannel.get().size(); i++) {
            usersArray[i] = usersInChannel.get().get(i).getId();
        }
        return ResponseEntity.ok(usersArray);
    }
}
