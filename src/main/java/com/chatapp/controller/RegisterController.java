package com.chatapp.controller;

import com.chatapp.entity.User;
import com.chatapp.service.MailService;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import com.chatapp.util.LoginOptions;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Optional;

@Controller
public class RegisterController {
    private final UserService userService;
    private final MailService mailService;
    private final SmsService smsService;

    @Autowired
    public RegisterController(UserService userService, MailService mailService, SmsService smsService) {
        this.userService = userService;
        this.mailService = mailService;
        this.smsService = smsService;
    }

    @GetMapping("/signUp")
    public String getSingUpPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("login_option") String loginOption,
                           @RequestParam("username") String username,
                           Model model) throws MessagingException, UnsupportedEncodingException {
        if (username == null) return "register";
        Optional<User> checkUser = userService.findByEmailOrPhone(username);
        if (checkUser.isPresent() && checkUser.get().getIsActive()) {
            if (Objects.equals(loginOption, "PHONE"))
                model.addAttribute("error", "Someone already registered with this phone number");
            else model.addAttribute("error", "Someone already registered with this email");
        } else {
            if (Objects.equals(loginOption, "EMAIL")) {
                User newUser = userService.createNewUser(username, LoginOptions.EMAIL);
                mailService.sendActivationNumber(username, newUser.getActivationNumber());
            } else {
                User newUser = userService.createNewUser(username, LoginOptions.PHONE);
                smsService.sendActivationSms(username, newUser.getActivationNumber());
            }
        }
        return "register";
    }

    @PostMapping("/activate")
    public String activate(@RequestParam("activation_number") String activationNumber,
                           @RequestParam("username") String username,
                           Model model) {
        Optional<User> checkedUser = userService.findByEmailOrPhone(username);
        if (checkedUser.isPresent()) {
            if (checkedUser.get().getIsActive()) return "redirect:/login";
            if (userService.activateUser(username, activationNumber)) return "redirect:/login";
        } else {
            model.addAttribute("error", "Please register an account.");
        }
        return "redirect:/signUp";
    }
}
