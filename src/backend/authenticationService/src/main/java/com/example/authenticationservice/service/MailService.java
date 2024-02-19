package com.example.authenticationservice.service;

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

    @Value("${spring.mail.default-receiver}")
    private String to;

    private final JavaMailSender mailSender;

    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    /* Public Methods */

    public void sendNewLoginProfileMail(String idLoginProfile, String password) throws Exception {
        // prepare mail
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject("Welcome to PROCOM-ERP");
        mail.setText(buildContent(idLoginProfile, password));

        // send mail
        sendMail(mail);

    }

    /* Private Methods */

    private void sendMail(SimpleMailMessage mail) throws Exception {

        logger.info("Send mail...");
        try {
            mailSender.send(mail);
            logger.info("Mail sent successfully");
        } catch (Exception e) {
            logger.error("Fail to send mail : " + e.getMessage(), e);
            throw new Exception();
        }

    }

    private String buildContent(String idLoginProfile, String password) {
        String content = """
                Hello new colleague ! 😊

                Welcome to the company. Below, you will find your login information.
                
                - ID : %s
                - Password : %s (don't forget to change it ASAP ! 🔒)
                
                Best regards,
                
                The best company ever ! 🚀
                """;
        return String.format(content, idLoginProfile, password);
    }
}
