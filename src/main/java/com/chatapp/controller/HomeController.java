package com.chatapp.controller;

import com.chatapp.entity.Message;
import com.chatapp.entity.RegisteredChannel;
import com.chatapp.entity.User;
import com.chatapp.service.RegisteredChannelService;
import com.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    private final UserService userService;
    private final RegisteredChannelService registeredChannelService;

    @Autowired
    public HomeController(UserService userService, RegisteredChannelService registeredChannelService) {
        this.userService = userService;
        this.registeredChannelService = registeredChannelService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("username", currentUser.getUsername());
        return "home";
    }

    @GetMapping("/getChannels")
    public ResponseEntity<List<RegisteredChannel>> getMessagedPeople() {
        User currentUser = userService.getCurrentUser();
        Optional<List<RegisteredChannel>> registeredChannels = Optional.empty();
        if (currentUser != null) {
            registeredChannels = registeredChannelService.getRegisteredChannelsByIdDesc(currentUser);
        }
        return registeredChannels.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
    }


    //Continuous here
    @GetMapping("/getMessages")
    public ResponseEntity<Message> getMessagesBetweenUsers() {
        User currentUser = userService.getCurrentUser();
        List<RegisteredChannel> registeredChannels = currentUser.getRegisteredChannels();
        for (RegisteredChannel channel : registeredChannels) {
            List<Message> messages = channel.getChannel().getMessages();
        }
        return null;
    }
}
