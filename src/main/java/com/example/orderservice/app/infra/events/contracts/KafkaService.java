package com.example.orderservice.app.infra.events.contracts;

import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.orderservice.app.infra.events.entities.Event;
import com.example.orderservice.app.infra.events.interfaces.EventService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Primary
public class KafkaService implements EventService {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    private static final String TOPIC = "order-events";

    @Override
    public void publishEvent(Event event) {
        kafkaTemplate.send(TOPIC, event.getId(), event);
    }
}
