package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.UserEvent;
import com.example.notificationservice.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
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
            mail.sendAccountCreated(event.getEmail());
        } else if (event.getOperation() == UserEvent.Operation.DELETED) {
            mail.sendAccountDeleted(event.getEmail());
        }
    }
}