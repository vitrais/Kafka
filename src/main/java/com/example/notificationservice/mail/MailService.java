package com.example.notificationservice.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void send(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    public void sendAccountCreated(String to) {
        send(to, "Account created", "Your account has been created successfully.");
    }

    public void sendAccountCreated(String to, String name) {
        String subject = "Account created";
        String text = "Hello, %s!\n\nYour account has been created successfully.\n\nBest regards,\nYour Team"
                .formatted(name);
        send(to, subject, text);
    }

    public void sendAccountDeleted(String to) {
        String subject = "Account deleted";
        String text = "Your account has been deleted successfully.\n\nIf this was a mistake, please contact support.";
        send(to, subject, text);
    }

    public void sendAccountCreatedRu(String to) {
        send(to, "Аккаунт создан", "Здравствуйте! Ваш аккаунт был успешно создан.");
    }

    public void sendAccountCreatedRu(String to, String name) {
        send(to, "Аккаунт создан", "Здравствуйте, %s! Ваш аккаунт был успешно создан."
                .formatted(name));
    }

    public void sendAccountDeletedRu(String to) {
        send(to, "Аккаунт удалён", "Здравствуйте! Ваш аккаунт был удалён.");
    }
}

