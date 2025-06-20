package com.chatapp.controller;

import com.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {
    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/create-ott")
    public String createOtt() {
        return "index";
    }

    @GetMapping("/login/ott")
    public String login() {
        return "index";
    }
}
