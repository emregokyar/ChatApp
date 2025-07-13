package com.chatapp.controller;

import com.chatapp.entity.Channel;
import com.chatapp.entity.RegisteredChannel;
import com.chatapp.entity.User;
import com.chatapp.service.*;
import com.chatapp.util.ChannelType;
import com.chatapp.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ContactAndGroupController {
    private final UserService userService;
    private final ContactService contactService;
    private final ChannelService channelService;
    private final RegisteredChannelService registeredChannelService;
    private final GroupRoleService groupRoleService;

    @Autowired
    public ContactAndGroupController(UserService userService, ContactService contactService, ChannelService channelService, RegisteredChannelService registeredChannelService, GroupRoleService groupRoleService) {
        this.userService = userService;
        this.contactService = contactService;
        this.channelService = channelService;
        this.registeredChannelService = registeredChannelService;
        this.groupRoleService = groupRoleService;
    }

    @PostMapping("/addNewContact")
    public String addNewContact(Principal principal,
                                @ModelAttribute("nickname") String name,
                                @ModelAttribute("username") String username) {
        User currentUser = userService.getCurrentUser();
        if (!Objects.equals(currentUser.getUsername(), principal.getName())) return "redirect:/login";
        Optional<User> contactedUser = userService.findByEmailOrPhone(username);

        if (Objects.equals(name, "")) return "redirect:/home";
        if (contactedUser.isPresent() && (contactService.getContact(currentUser.getId(), contactedUser.get().getId()).isEmpty())) {
            contactService.createNewContact(currentUser, contactedUser.get(), name);
        }
        return "redirect:/home";
    }

    @PostMapping("/createGroup")
    public String createNewGroup(Principal principal,
                                 @ModelAttribute("userIds") int[] userIds,
                                 @ModelAttribute("subject") String subject,
                                 @ModelAttribute("groupPhoto") MultipartFile groupPhoto) {
        User currentUser = userService.getCurrentUser();
        if (!Objects.equals(currentUser.getUsername(), principal.getName())) return "redirect:/login";

        Channel newChannel = Channel.builder()
                .type(ChannelType.GROUP)
                .subject(subject)
                .build();

        String imageName = "";
        if (!Objects.equals(groupPhoto.getOriginalFilename(), "")) {
            imageName = StringUtils.cleanPath(Objects.requireNonNull(groupPhoto.getOriginalFilename()));
            newChannel.setGroupPhoto(imageName);
        }

        Channel savedChannel = channelService.createChannel(newChannel);
        try {
            String uploadDir = "photos/channels/" + savedChannel.getId() + "/channel_photo";
            if (!Objects.equals(groupPhoto.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, imageName, groupPhoto);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred when uploading a group photo: " + e);
        }

        RegisteredChannel currentUserRegister = RegisteredChannel.builder()
                .channel(savedChannel)
                .user(currentUser)
                .role(groupRoleService.getAdminRole())
                .build();
        registeredChannelService.register(currentUserRegister);

        Arrays.stream(userIds).forEach((userId) -> {
            Optional<User> user = userService.findByUserId(userId);
            if (user.isPresent()) {
                RegisteredChannel registry = RegisteredChannel.builder()
                        .channel(savedChannel)
                        .user(user.get())
                        .role(groupRoleService.getRegularRole())
                        .build();
                registeredChannelService.register(registry);
            }
        });

        return "redirect:/home";
    }
}
