package com.chatapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class MailService {
    private final JavaMailSender javaMailSender;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendPassword(String recipientEmail, String password) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("emregokyar1940@gmail.com", "WhatsApp Clone Ott Login");
        helper.setTo(recipientEmail);

        String subject = "Here password to login your account";
        String content = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\n" +
                "  <div style=\"margin:50px auto;width:70%;padding:20px 0\">\n" +
                "    <div style=\"border-bottom:1px solid #eee\">\n" +
                "      <a href=\"\" style=\"font-size:1.4em;color: #008069;text-decoration:none;font-weight:600\">Wp Clone</a>\n" +
                "    </div>\n" +
                "    <p style=\"font-size:1.1em\">Hi,</p>\n" +
                "    <p>Thank you for choosing WhatsApp Clone. Use the following OTP to complete your Sign Up procedures. OTP is valid for 5 minutes</p>\n" +
                "    <h2 style=\"background: #008069;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" + password + "</h2>\n" +
                "    <p style=\"font-size:0.9em;\">Regards,<br />WhatsApp Clone</p>\n" +
                "    <hr style=\"border:none;border-top:1px solid #eee\" />\n" +
                "    <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\n" +
                "      <p>Fahrettin     Emre Gokyar</p>\n" +
                "      <p>Istanbul, Turkey</p>\n" +
                "      <p>34752</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>";

        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }
}
