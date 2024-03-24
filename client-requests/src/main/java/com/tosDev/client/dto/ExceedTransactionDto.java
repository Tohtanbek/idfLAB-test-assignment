package com.tosDev.client.dto;

import com.tosDev.client.util.json_serializer.CurrencySerializer;
import com.tosDev.client.util.json_serializer.OffsetDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tosDev.tr.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceedTransactionDto {

    private Long accountFrom;
    private Long accountTo;

    @JsonProperty("sum")
    private Double trSum;

    @JsonProperty("currency_shortname")
    @JsonSerialize(using = CurrencySerializer.class)
    private Currency trCurrency;

    private ExpenseCategory expenseCategory;

    @JsonProperty("datetime")
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime trDateTime;

    @JsonProperty("limit_sum")
    private Double historicalLimitSum;

    @JsonProperty("limit_datetime")
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime limitSetDateTime;

    @JsonProperty("limit_currency_shortname")
    @JsonSerialize(using = CurrencySerializer.class)
    private Currency limitCurrency;
}
