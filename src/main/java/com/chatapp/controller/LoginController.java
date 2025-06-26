package com.chatapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/create-ott")
    public String createOtt() {
        return "login";
    }

    @GetMapping("/login/ott")
    public String login() {
        return "login";
    }
}
