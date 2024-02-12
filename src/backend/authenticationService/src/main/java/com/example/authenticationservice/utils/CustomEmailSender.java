package com.example.authenticationservice.utils;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomEmailSender {

    @Value("${spring.mail.username}")
    private String from;

    private final String to = "procom.erp@gmail.com";

    private final JavaMailSender mailSender;

    private final Logger logger = LoggerFactory.getLogger(CustomEmailSender.class);

    public void sendNewLoginProfileMail(String idLoginProfile, String password) {
        // prepare mail
        logger.info("Prepare mail...");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Welcome to PROCOM-ERP");
        message.setText(buildContent(idLoginProfile, password));

        // send mail
        logger.info("Send mail...");
        try {
            mailSender.send(message);
            logger.info("Mail sent successfully");
        } catch (Exception e) {
            logger.error("Fail to send mail : " + e.getMessage(), e);
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
