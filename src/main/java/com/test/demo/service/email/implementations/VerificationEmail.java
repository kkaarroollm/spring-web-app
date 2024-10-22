package com.test.demo.service.email.implementations;

import com.test.demo.service.email.BaseEmail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
public class VerificationEmail extends BaseEmail {

    public VerificationEmail(JavaMailSender mailSender, TemplateEngine templateEngine) {
        super(mailSender, templateEngine);
    }

    @Override
    public String getSubject() {
        return "Verify your email";
    }

    @Override
    public String getTemplateLocation() {
        return "emails/verification";
    }

    @Override
    public String getRedirectUrl() {
        /* For development testing. */
        return "https://localhost:8080/verify-email";
    }
}



