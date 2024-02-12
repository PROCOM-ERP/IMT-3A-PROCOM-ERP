package com.example.gatewayService.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    public void sendMail(String to, String subject, String content) {
        // prepare mail
        logger.info("Prepare mail before sending...");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        // send mail
        logger.info("Send mail...");
        try {
            mailSender.send(message);
            logger.info("Mail sent successfully");
        } catch (Exception e) {
            logger.error("Fail to send mail : " + e.getMessage(), e);
        }

    }
}
