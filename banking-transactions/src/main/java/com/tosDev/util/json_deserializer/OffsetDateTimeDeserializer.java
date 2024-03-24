package com.tosDev.util.json_deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        DateTimeFormatter offsetDateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX");
        return OffsetDateTime.parse(p.getValueAsString(),offsetDateTimeFormatter);
    }
}
