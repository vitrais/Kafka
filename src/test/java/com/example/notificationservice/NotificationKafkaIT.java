package com.example.notificationservice;

import com.example.notificationservice.dto.UserEvent;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = { "users.events" },
        brokerProperties = { "listeners=PLAINTEXT://localhost:0", "port=0" }
)
@TestPropertySource(properties = {
        "app.kafka.enabled=true",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "app.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "app.kafka.topic.users-events=users.events",

        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.protocol=smtp",
        "spring.mail.properties.mail.smtp.auth=false",
        "spring.mail.properties.mail.smtp.starttls.enable=false",
        "spring.mail.properties.mail.smtp.ssl.enable=false",
        "spring.mail.properties.mail.transport.protocol=smtp"
})
@ActiveProfiles("test")
@DirtiesContext
class NotificationKafkaIT {

    @RegisterExtension
    static GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP.withPort(3025))
                    .withPerMethodLifecycle(true);

    @Value("${spring.embedded.kafka.brokers}")
    String brokers;

    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @BeforeEach
    void setUp() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
        kafkaTemplate.setDefaultTopic("users.events");
    }

    @Test
    void shouldConsumeUserCreatedEventAndSendMail() throws Exception {
        UserEvent event = new UserEvent();
        event.setOperation(UserEvent.Operation.CREATED);
        event.setUserId(123L);
        event.setEmail("test.receiver@example.com");
        event.setName("Test User");
        event.setOccurredAt(Instant.now());

        kafkaTemplate.sendDefault(event).get(); // дождаться отправки

        greenMail.waitForIncomingEmail(10_000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();

        assertThat(messages).hasSize(1);
        assertThat(messages[0].getAllRecipients()[0].toString())
                .isEqualTo("test.receiver@example.com");
    }
}

