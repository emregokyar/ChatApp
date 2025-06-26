package com.chatapp.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String phoneNumber;

    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void sendLoginSms(String recipientNumber, String code) {
        initTwilio();
        String message = "YourAppName Login Code:\n"
                + "Use the code below to log in to your account:\n\n"
                + code + "\n\n"
                + "Do not share this code with anyone. It will expire in 5 minutes.";
        Message.creator(new PhoneNumber(recipientNumber), new PhoneNumber(phoneNumber), message).create();
    }

    public void sendActivationSms(String recipientNumber, String code) {
        initTwilio();
        String message = "YourAppName Activation Code:\n"
                + "Use the code below to activate your account:\n\n"
                + code + "\n\n"
                + "Do not share this code with anyone.";
        Message.creator(new PhoneNumber(recipientNumber), new PhoneNumber(phoneNumber), message).create();
    }
}
