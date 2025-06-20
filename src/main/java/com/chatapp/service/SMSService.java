package com.chatapp.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Component;

@Component
public class SMSService {

    public void sendSms(String recipientNumber, String code) {
        String message = "YourAppName Login Code:\n"
                + "Use the code below to log in to your account:\n\n"
                + code + "\n\n"
                + "Do not share this code with anyone. It will expire in 5 minutes.";

        Message.creator(new PhoneNumber(recipientNumber), new PhoneNumber("+905523081940"), message).create();
    }
}
