package com.example.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class FakeMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        // Возвращаем пустой sender, чтобы сервис не падал
        return new JavaMailSenderImpl();
    }
}
