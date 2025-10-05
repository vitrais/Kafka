package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.UserEvent;
import com.example.notificationservice.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class UserEventListener {

    private final MailService mail;

    @KafkaListener(
            topics = "${app.kafka.topic.users-events:users.events}",
            groupId = "notification-service",
            containerFactory = "userEventKafkaListenerContainerFactory"
    )
    public void onUserEvent(UserEvent event) {
        log.info("Got UserEvent: {}", event);
        if (event.getOperation() == UserEvent.Operation.CREATED) {
            mail.sendAccountCreated(event.getEmail()); // EN-вариант ок для Kafka IT; RU нужен только в API IT
        } else if (event.getOperation() == UserEvent.Operation.DELETED) {
            mail.sendAccountDeleted(event.getEmail());
        }
    }
}
