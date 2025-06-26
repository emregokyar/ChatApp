package com.chatapp.config;

import com.chatapp.entity.User;
import com.chatapp.service.CustomUserDetailService;
import com.chatapp.service.MailService;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import com.chatapp.util.LoginOptions;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomOttGenerationSuccessHandler implements OneTimeTokenGenerationSuccessHandler {
    private final MailService mailService;
    private final CustomUserDetailService userDetailService;
    private final SmsService smsService;
    private final UserService userService;
    //Creating custom redirect page after a user sends an email - in my case, I am redirecting index page
    private final OneTimeTokenGenerationSuccessHandler redirectHandler = new RedirectOneTimeTokenGenerationSuccessHandler("/login");

    @Autowired
    public CustomOttGenerationSuccessHandler(MailService mailService, CustomUserDetailService userDetailService, SmsService smsService, UserService userService) {
        this.mailService = mailService;
        this.userDetailService = userDetailService;
        this.smsService = smsService;
        this.userService = userService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken)
            throws IOException, ServletException {
        if (!isUserActive(oneTimeToken.getUsername())) return;

        try {
            String username = getUsername(oneTimeToken.getUsername());

            if (getUserRegistrationType(username) == LoginOptions.EMAIL) {
                mailService.sendPassword(username, oneTimeToken.getTokenValue());
            } else if (getUserRegistrationType(username) == LoginOptions.PHONE) {
                smsService.sendLoginSms(username, oneTimeToken.getTokenValue());
            }
        } catch (MessagingException e) {
            throw new RuntimeException("There was an error while sending otp to user: " + e);
        }
        redirectHandler.handle(request, response, oneTimeToken);
    }

    private String getUsername(String username) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        return userDetails.getUsername();
    }

    private LoginOptions getUserRegistrationType(String username) {
        Optional<User> user = userService.findByEmailOrPhone(username);
        return user.map(User::getRegistrationType).orElse(null);
    }

    private boolean isUserActive(String username) {
        Optional<User> user = userService.findByEmailOrPhone(username);
        boolean isActive = false;
        if (user.isPresent()) {
            isActive = user.get().getIsActive();
        }
        return isActive;
    }
}