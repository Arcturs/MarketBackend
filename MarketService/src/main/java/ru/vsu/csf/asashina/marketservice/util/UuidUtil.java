package ru.vsu.csf.asashina.marketservice.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidUtil {

    public String generateRandomUUIDInString() {
        return UUID.randomUUID().toString();
    }
}
