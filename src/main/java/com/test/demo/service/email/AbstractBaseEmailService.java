package com.test.demo.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public abstract class AbstractBaseEmailService implements EmailService {

    @Autowired
    protected JavaMailSender mailSender;

    @Autowired
    protected TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    protected String fromMail;

    protected Context context;

    public AbstractBaseEmailService() {
        this.context = new Context();
    }

    @Override
    public void sendEmail(String[] recipients) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromMail);
        mimeMessageHelper.setTo(recipients);
        mimeMessageHelper.setSubject(getSubject());
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
