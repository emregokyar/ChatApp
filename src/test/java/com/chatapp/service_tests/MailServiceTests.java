package com.chatapp.service_tests;

import com.chatapp.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.UnsupportedEncodingException;

@SpringBootTest(classes = MailService.class)
@TestPropertySource("/application-test.properties")
public class MailServiceTests {
    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MailService mailService;

    @Test
    void testSendPassword() throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage((Session) null);
        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(message);

        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo("emregokyar1940@gmail.com");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(message);
        mailService.sendPassword("emregokyar1940@gmail.com", "12345678");
        Mockito.verify(javaMailSender).send(message);
    }

    @Test
    void testSendActivationNumber() throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage((Session) null);
        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(message);

        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo("emregokyar1940@gmail.com");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(message);
        mailService.sendActivationNumber("emregokyar1940@gmail.com", "12345678");
        Mockito.verify(javaMailSender).send(message);
    }
}
