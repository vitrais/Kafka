package com.example.notificationservice;

import com.example.notificationservice.dto.UserEvent;
import com.example.notificationservice.mail.MailService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import java.time.Duration;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = { "users.events" }, // важно: то же имя, которое слушает ваш @KafkaListener
        brokerProperties = { "listeners=PLAINTEXT://localhost:0", "port=0" }
)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "app.kafka.topic.users-events=users.events"
})
@DirtiesContext
class NotificationKafkaIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @MockBean
    private MailService mailService;

    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @BeforeEach
    void setUp() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, UserEvent> pf = new DefaultKafkaProducerFactory<>(producerProps);
        kafkaTemplate = new KafkaTemplate<>(pf);
        kafkaTemplate.setDefaultTopic("users.events"); // тот же топик
    }

    @Test
    void shouldConsumeUserCreatedEventAndSendMail() {

        UserEvent event = new UserEvent();
        event.setOperation(UserEvent.Operation.CREATED);
        event.setUserId(123L);
        event.setEmail("test.receiver@example.com");
        event.setName("Test User");
        event.setOccurredAt(Instant.now());

        kafkaTemplate.sendDefault(event);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                Mockito.verify(mailService).sendAccountCreated("test.receiver@example.com")
        );
    }
}
