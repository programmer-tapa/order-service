package com.example.orderservice.app.infra.events.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Event {
    private String id;
    private String name;
    private Object data;
}
