package com.test.demo.service.email;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String[] recipients) throws MessagingException;
    void sendEmail(String recipient) throws MessagingException;
    String getSubject();
    String getTemplateLocation();
    String getRedirectUrl();
}
