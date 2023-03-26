package ru.vsu.csf.asashina.marketservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static ru.vsu.csf.asashina.marketservice.model.constant.Operation.DELETE;

@Component(DELETE)
@Slf4j
public class DeleteProductChangeEventHandler implements ProductChangeEventHandler {

    @Override
    public void logChangeEvent(Map<String, Object> payload) {
        log.info("Deleted product with id {}", payload.get("product_id"));
    }
}
