package com.example.orderservice.app.infra.events.contracts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.orderservice.app.infra.events.entities.Event;
import com.example.orderservice.app.infra.events.interfaces.EventService;

@Service
@Primary
public class KafkaService implements EventService {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    public KafkaService(KafkaTemplate<String, Event> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishEvent(Event event) {
        kafkaTemplate.send(topic, event.getId(), event);
    }
}
