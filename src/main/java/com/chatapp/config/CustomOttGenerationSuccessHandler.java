package com.chatapp.config;

import com.chatapp.service.CustomUserDetailService;
import com.chatapp.service.MailService;
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

@Component
public class CustomOttGenerationSuccessHandler implements OneTimeTokenGenerationSuccessHandler {
    private final MailService mailService;
    private final CustomUserDetailService userDetailService;
    //Creating custom redirect page after a user sends an email - in my case, I am redirecting index page
    private final OneTimeTokenGenerationSuccessHandler redirectHandler = new RedirectOneTimeTokenGenerationSuccessHandler("/");

    @Autowired
    public CustomOttGenerationSuccessHandler(MailService mailService, CustomUserDetailService userDetailService) {
        this.mailService = mailService;
        this.userDetailService = userDetailService;
    }

    //Here, the link can be built and send the link to the user email directly
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken)
            throws IOException, ServletException {
        //Send the ott to user
        try {
            String email = getUserEmail(oneTimeToken.getUsername());
            mailService.sendPassword(email, oneTimeToken.getTokenValue());
        } catch (MessagingException e) {
            throw new RuntimeException("There was an error while sending otp to user: " + e);
        }
        redirectHandler.handle(request, response, oneTimeToken);
    }

    //Retrieving the user from DB
    private String getUserEmail(String username) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        return userDetails.getUsername();
    }
}