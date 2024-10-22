package com.test.demo.service.email.implementations;


import com.test.demo.model.User;
import com.test.demo.service.email.AbstractBaseEmailService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class VerificationEmailService extends AbstractBaseEmailService {

    private Logger logger = Logger.getLogger(VerificationEmailService.class.getName());
    private String token;
    User user;

    public VerificationEmailService() {
        super();
    }

    public VerificationEmailService setToken(String token) {
        this.token = token;
        this.context.setVariable("token", token);
        return this;
    }

    public VerificationEmailService setUser(User user) {
        this.user = user;
        this.context.setVariable("user", user);
        return this;
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
        return "http://localhost:8080/verify-email?token=" + token;
    }

    @Override
    public void sendEmail(String[] recipients) {
        if (user == null || token == null) {
            throw new IllegalStateException("User and token must be set before sending email.");
        }
        try {
            super.sendEmail(recipients);
        } catch (MessagingException e) {
            logger.severe("Failed to send email to " + user.getEmail() + e);
            throw new RuntimeException("Failed to send email to " + user.getEmail(), e);
        }
    }
}



