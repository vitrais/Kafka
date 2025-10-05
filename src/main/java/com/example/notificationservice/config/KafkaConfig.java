package com.example.notificationservice.config;

import com.example.notificationservice.dto.UserEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.*;

@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, UserEvent> userEventConsumerFactory(org.springframework.core.env.Environment env) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("app.kafka.bootstrap-servers", "localhost:9092"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        var valueDeserializer = new JsonDeserializer<>(UserEvent.class);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEvent> userEventKafkaListenerContainerFactory(
            ConsumerFactory<String, UserEvent> cf) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, UserEvent>();
        factory.setConsumerFactory(cf);
        factory.setConcurrency(1);
        return factory;
    }
}