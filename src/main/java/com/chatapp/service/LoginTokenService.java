package com.chatapp.service;

import com.chatapp.entity.LoginToken;
import com.chatapp.entity.User;
import com.chatapp.repository.LoginTokenRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ott.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class LoginTokenService implements OneTimeTokenService {
    private final LoginTokenRepository loginTokenRepository;
    private final OttExtrasService ottExtrasService;
    private final UserService userService;
    private final long expiryInterval = 5L * 60 * 1000;

    @Autowired
    public LoginTokenService(LoginTokenRepository loginTokenRepository, OttExtrasService ottExtrasService, UserService userService) {
        this.loginTokenRepository = loginTokenRepository;
        this.ottExtrasService = ottExtrasService;
        this.userService = userService;
    }

    @NonNull
    @Override
    public OneTimeToken generate(GenerateOneTimeTokenRequest request) {
        Optional<User> user = userService.findByEmailOrPhone(request.getUsername());
        String token = ottExtrasService.createRandomOneTimePassword().get();
        Date expiry = new Date(System.currentTimeMillis() + expiryInterval);

        LoginToken loginToken = new LoginToken();
        loginToken.setExpirationDate(expiry);
        loginToken.setToken(token);
        user.ifPresent(loginToken::setUser);
        return loginTokenRepository.save(loginToken);
    }

    @Override
    public OneTimeToken consume(OneTimeTokenAuthenticationToken authenticationToken) {
        Optional<LoginToken> ott = loginTokenRepository.findByToken(authenticationToken.getTokenValue());
        if (ott.isPresent()) {
            LoginToken loginToken = ott.get();
            if (!isExpired(loginToken)) {
                loginTokenRepository.delete(loginToken);
                return new DefaultOneTimeToken(loginToken.getTokenValue(), loginToken.getUsername(), loginToken.getExpiresAt());
            }
            loginTokenRepository.delete(loginToken);
        }
        return null;
    }

    private boolean isExpired(LoginToken ott) {
        Date currentDate = new Date(System.currentTimeMillis());
        Date expirationDate = ott.getExpirationDate();
        return currentDate.after(expirationDate);
    }
}
