package com.tosDev.dto;

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
public class ExchangeRateDto {
    private Currency currency;
    private Double actualRate;
    private OffsetDateTime accessDateTime;
}
