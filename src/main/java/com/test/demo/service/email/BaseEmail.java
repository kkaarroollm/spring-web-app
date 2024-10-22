package com.test.demo.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public abstract class BaseEmail implements EmailService {
    protected final JavaMailSender mailSender;
    protected final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    protected String fromMail;

    public BaseEmail(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(String[] recipients) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromMail);
        mimeMessageHelper.setTo(recipients);
        mimeMessageHelper.setSubject(getSubject());

        Context context = new Context();
        context.setVariable("redirectUrl", getRedirectUrl());
        String processedString = templateEngine.process(getTemplateLocation(), context);
        mimeMessageHelper.setText(processedString, true);

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendEmail(String recipient) throws MessagingException {
        sendEmail(new String[]{recipient});
    }
}
