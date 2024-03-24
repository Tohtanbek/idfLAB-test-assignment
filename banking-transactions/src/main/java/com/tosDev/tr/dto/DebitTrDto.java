package com.tosDev.tr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tosDev.tr.enums.ExpenseCategory;
import com.tosDev.tr.util.json_deserializer.CurrencyDeserializer;
import com.tosDev.tr.util.json_deserializer.OffsetDateTimeDeserializer;
import com.tosDev.tr.util.json_deserializer.ProductDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DebitTrDto {
    private Long accountFrom;
    private Long accountTo;

    @JsonDeserialize(using = CurrencyDeserializer.class)
    @JsonProperty("currency_shortname")
    private Currency currency;

    private Double sum;

    @JsonDeserialize(using = ProductDeserializer.class)
    private ExpenseCategory expenseCategory;

    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @JsonProperty("datetime")
    private OffsetDateTime dateTime;

}
