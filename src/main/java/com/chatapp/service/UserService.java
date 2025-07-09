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
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OttExtrasService ottExtrasService;

    @Autowired
    public UserService(UserRepository userRepository, OttExtrasService ottExtrasService) {
        this.userRepository = userRepository;
        this.ottExtrasService = ottExtrasService;
    }

    public Optional<User> findByEmailOrPhone(String input) {
        return userRepository.findByUsername(input);
    }

    public User createNewUser(String username, LoginOptions loginOption) {
        Optional<User> user = userRepository.findByUsername(username);
        String activationNumber = ottExtrasService.createRandomOneTimePassword().get();

        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setRegistrationType(loginOption);
            newUser.setUsername(username);
            newUser.setIsPrivate(false);
            newUser.setIsActive(false);
            newUser.setRegistrationDate(new Date(System.currentTimeMillis()));
            newUser.setActivationNumber(activationNumber);
            return userRepository.save(newUser);
        } else {
            user.get().setActivationNumber(activationNumber);
            return userRepository.save(user.get());
        }
    }

    public boolean activateUser(String username, String token) {
        boolean result = false;
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()
                && !user.get().getIsActive()
                && Objects.equals(user.get().getActivationNumber(), token)) {
            user.get().setIsActive(true);
            user.get().setActivationNumber(null);
            userRepository.save(user.get());
            result = true;
        }
        return result;
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

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByUserId(Integer userId) {
        return userRepository.findById(userId);
    }
}
