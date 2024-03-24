package com.tosDev.util.json_deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.tosDev.enums.ExpenseCategory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Currency;

public class ProductDeserializer extends JsonDeserializer<ExpenseCategory> {

    @Override
    public ExpenseCategory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String expenseCategory = p.getValueAsString();

        return Arrays.stream(ExpenseCategory.values())
                .filter(desc -> desc.getDescription().equals(expenseCategory))
                .findFirst()
                .orElseThrow();
    }
}
