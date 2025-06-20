package com.chatapp.controller;

import com.chatapp.entity.User;
import com.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("username", currentUser.getUsername());
        return "home";
    }
}
