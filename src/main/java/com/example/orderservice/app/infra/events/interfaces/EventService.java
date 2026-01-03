package com.example.orderservice.app.infra.events.interfaces;

import com.example.orderservice.app.infra.events.entities.Event;

public interface EventService {

    void publishEvent(Event event);

}
