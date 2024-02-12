package com.example.authenticationservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomEmailSender {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.default-receiver}")
    private String to;

    private final JavaMailSender mailSender;

    public void sendNewLoginProfileMail(String idLoginProfile, String password) {
        // prepare mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Welcome to PROCOM-ERP");
        message.setText(buildContent(idLoginProfile, password));

        // send mail
        try {
            mailSender.send(message);
        } catch (MailException ignored) {
        }

    }

    private String buildContent(String idLoginProfile, String password) {
        String content = """
                Hello new colleague! 😊

                Welcome to the company. Below, you will find your login information.
                
                - ID : %s
                - Password : %s (don't forget to change it ASAP! 🔒)
                
                Best regards,
                
                The best company ever! 🚀
                """;
        return String.format(content, idLoginProfile, password);
    }
}
