package com.tosDev.client.util.json_serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Currency;


public class CurrencySerializer extends JsonSerializer<Currency> {

    @Override
    public void serialize(Currency value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getCurrencyCode());
    }
}
