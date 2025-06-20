package com.chatapp.service;

import com.chatapp.repository.LoginTokenRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;
import java.util.function.Supplier;

@Service
@EnableAsync
@Component
public class OttExtrasService {
    private final LoginTokenRepository tokenRepository;
    private final static Integer LENGTH = 8;

    public OttExtrasService(LoginTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Supplier<String> createRandomOneTimePassword() {
        return () -> {
            Random random = new Random();
            StringBuilder oneTimePassword = new StringBuilder();
            for (int i = 0; i < LENGTH; i++) {
                int randomNumber = random.nextInt(10);
                oneTimePassword.append(randomNumber);
            }
            return oneTimePassword.toString().trim();
        };
    }

    //Deleting expired tokens every hour
    @Scheduled(fixedRate = 3600000)
    @Transactional
    @Async
    public void cleanUpOldRecords() {
        tokenRepository.deleteExpiredTokens(new Date(System.currentTimeMillis()));
    }
}
