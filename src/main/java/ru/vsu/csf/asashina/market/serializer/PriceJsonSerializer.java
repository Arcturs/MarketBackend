package ru.vsu.csf.asashina.market.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PriceJsonSerializer extends JsonSerializer<Float> {

    private static final int AMOUNTS_OF_DIGITS_AFTER_POINT = 2;

    @Override
    public void serialize(Float value, JsonGenerator generator, SerializerProvider serializers)
            throws IOException {
        generator.writeNumber(roundValue(value));
    }

    private float roundValue(float value) {
        int tenInPow = (int) Math.pow(10, AMOUNTS_OF_DIGITS_AFTER_POINT);
        float roundedValue = value * tenInPow;
        roundedValue = Math.round(roundedValue);
        return roundedValue / tenInPow;
    }
}
