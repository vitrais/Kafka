package com.example.notificationservice.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    /**
     * Базовый универсальный метод отправки писем.
     */
    public void send(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    /**
     * Письмо о создании аккаунта (старый и новый вариант).
     */
    public void sendAccountCreated(String to) {
        send(to, "Account created", "Your account has been created successfully.");
    }

    public void sendAccountCreated(String to, String name) {
        String subject = "Account created";
        String text = String.format(
                "Hello, %s!\n\nYour account has been created successfully.\n\nBest regards,\nYour Team",
                name
        );
        send(to, subject, text);
    }

    /**
     * Письмо об удалении аккаунта.
     */
    public void sendAccountDeleted(String to) {
        String subject = "Account deleted";
        String text = "Your account has been deleted successfully.\n\nIf this was a mistake, please contact support.";
        send(to, subject, text);
    }
}
