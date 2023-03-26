package ru.vsu.csf.asashina.marketservice.handler;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface ProductChangeEventHandler {

    void logChangeEvent(Map<String, Object> payload);
}
