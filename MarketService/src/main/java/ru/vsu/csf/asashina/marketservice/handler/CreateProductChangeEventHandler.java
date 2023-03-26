package ru.vsu.csf.asashina.marketservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static ru.vsu.csf.asashina.marketservice.model.constant.Operation.CREATE;

@Component(CREATE)
@Slf4j
public class CreateProductChangeEventHandler implements ProductChangeEventHandler {

    @Override
    public void logChangeEvent(Map<String, Object> payload) {
        log.info("Created new product {}", payload);
    }
}
