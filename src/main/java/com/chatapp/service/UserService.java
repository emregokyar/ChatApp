package com.chatapp.service;

import com.chatapp.entity.User;
import com.chatapp.repository.UserRepository;
import com.chatapp.util.LoginOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmailOrPhone(String input) {
        return userRepository.findByUsername(input);
    }

    public User createNewUser(String input) {
        Optional<User> user = userRepository.findByUsername(input);

        if (user.isEmpty()) {
            User newUser = new User();
            if (input.contains("@")) newUser.setRegistrationType(LoginOptions.EMAIL);
            else newUser.setRegistrationType(LoginOptions.PHONE);
            newUser.setUsername(input);
            newUser.setIsPrivate(false);
            newUser.setRegistrationDate(new Date(System.currentTimeMillis()));
            return userRepository.save(newUser);
        } else {
            return userRepository.save(user.get());
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            return userRepository.findByUsername(username).orElseThrow(() ->
                    new UsernameNotFoundException("Can not found a user associated with this name"));
        }
        return null;
    }
}
