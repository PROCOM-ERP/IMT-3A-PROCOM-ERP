package com.example.authenticationservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomMailBuilder {

    @Value("${spring.mail.default-sender}")
    private final String from = "procom.erp@gmail.com";

    @Value("${spring.mail.default-receiver}")
    private final String to = "procom.erp@gmail.com";

    public void forwardMailToGatewayForSending() {

    }

    public SimpleMailMessage buildNewLoginProfileMail(String idLoginProfile, String password) {
        // prepare mail
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject("Welcome to PROCOM-ERP");
        mail.setText(buildContent(idLoginProfile, password));
        return mail;

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
