package com.example.orderservice.app.infra.events.contracts;

import com.example.orderservice.app.infra.events.entities.Event;
import com.example.orderservice.app.infra.events.interfaces.EventService;

public class KafkaService implements EventService {

    @Override
    public void publishEvent(Event event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'publishEvent'");
    }

}
