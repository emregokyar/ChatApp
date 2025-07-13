package com.chatapp.controller;

import com.chatapp.dto.UserDto;
import com.chatapp.entity.Contact;
import com.chatapp.entity.User;
import com.chatapp.service.ContactService;
import com.chatapp.service.UserService;
import com.chatapp.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
public class HomeController {
    private final UserService userService;
    private final ContactService contactService;

    @Autowired
    public HomeController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model, Principal principal) {
        User currentUser = userService.getCurrentUser();
        if (!Objects.equals(currentUser.getUsername(), principal.getName())) return "redirect:/login";
        model.addAttribute("currentUser", currentUser);

        Optional<List<Contact>> allContacts = contactService.getAllContacts(currentUser.getId());
        allContacts.ifPresent(contacts -> model.addAttribute("contacts", contacts));
        return "home";
    }

    @PostMapping(value = "/updateFullName", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateFullName(@ModelAttribute("fullName") String fullName, Principal principal) {
        User currentUser = userService.getCurrentUser();
        if (Objects.equals(currentUser.getUsername(), principal.getName())) {
            currentUser.setFullName(fullName);
            User user = userService.updateUser(currentUser);
            return ResponseEntity.ok(
                    UserDto.builder().username(user.getUsername()).build()
            );
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/updateAbout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateAbout(@ModelAttribute("about") String about, Principal principal) {
        User currentUser = userService.getCurrentUser();
        if (Objects.equals(currentUser.getUsername(), principal.getName())) {
            currentUser.setAbout(about);
            User user = userService.updateUser(currentUser);
            return ResponseEntity.ok(
                    UserDto.builder().username(user.getUsername()).build()
            );
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/updateProfilePhoto", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateUserPhoto(@ModelAttribute("profilePhoto") MultipartFile profilePhoto, Principal principal) {
        User currentUser = userService.getCurrentUser();
        if (Objects.equals(currentUser.getUsername(), principal.getName())) {
            String imageName = "";
            if (!Objects.equals(profilePhoto.getOriginalFilename(), "")) {
                imageName = StringUtils.cleanPath(Objects.requireNonNull(profilePhoto.getOriginalFilename()));
                currentUser.setProfilePhoto(imageName);
            }
            User user = userService.updateUser(currentUser);

            try {
                String uploadDir = "photos/users/" + currentUser.getId() + "/profile_photos";
                if (!Objects.equals(profilePhoto.getOriginalFilename(), "")) {
                    FileUploadUtil.saveFile(uploadDir, imageName, profilePhoto);
                }
            } catch (IOException e) {
                log.error("Can not upload a file associated with this name: {}", String.valueOf(e));
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(UserDto.builder().username(user.getUsername()).build());
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping(value = "/changePrivacy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> changePrivacy(@ModelAttribute("privacy") Boolean checked, Principal principal) {
        User currentUser = userService.getCurrentUser();
        if (Objects.equals(currentUser.getUsername(), principal.getName())) {
            currentUser.setIsPrivate(checked);
            User user = userService.updateUser(currentUser);
            return ResponseEntity.ok(
                    UserDto.builder().username(user.getUsername()).build()
            );
        }
        return ResponseEntity.badRequest().build();
    }
}
