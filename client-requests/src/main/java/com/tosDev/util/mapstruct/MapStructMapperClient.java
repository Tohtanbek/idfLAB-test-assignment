package com.tosDev.util.mapstruct;

import com.tosDev.dto.ExceedTransactionDto;
import com.tosDev.spring.jpa.entity.DebitTransaction;
import com.tosDev.spring.jpa.entity.ProductMonthlyLimit;
import com.tosDev.spring.jpa.entity.ServiceMonthlyLimit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Currency;

@Mapper(componentModel = "spring")
public abstract class MapStructMapperClient {

    @Mapping(source = "dt.accountFrom",target = "accountFrom")
    @Mapping(source = "dt.accountTo",target = "accountTo")
    @Mapping(source = "dt.currencyCode",target = "trCurrency",qualifiedByName = "dtCurrCharToCurrObj")
    @Mapping(source = "dt.sum",target = "trSum")
    @Mapping(source = "dt.expenseCategory",target = "expenseCategory")
    @Mapping(source = "dt",target = "trDateTime", qualifiedByName = "daoDTIntoDtoDT")
    @Mapping(source = "pl.currentLimit",target = "historicalLimitSum")
    @Mapping(source = "pl.changeDateTime",target = "limitSetDateTime",qualifiedByName = "utcDtToOffDt")
    public abstract ExceedTransactionDto fromTrAndProdLimit(DebitTransaction dt, ProductMonthlyLimit pl);


    @Mapping(source = "dt.accountFrom",target = "accountFrom")
    @Mapping(source = "dt.accountTo",target = "accountTo")
    @Mapping(source = "dt.currencyCode",target = "trCurrency",qualifiedByName = "dtCurrCharToCurrObj")
    @Mapping(source = "dt.sum",target = "trSum")
    @Mapping(source = "dt.expenseCategory",target = "expenseCategory")
    @Mapping(source = "dt",target = "trDateTime", qualifiedByName = "daoDTIntoDtoDT")
    @Mapping(source = "sl.currentLimit",target = "historicalLimitSum")
    @Mapping(source = "sl.changeDateTime",target = "limitSetDateTime",qualifiedByName = "utcDtToOffDt")
    public abstract ExceedTransactionDto fromTrAndServiceLimit(DebitTransaction dt, ServiceMonthlyLimit sl);

    @Named("dtCurrCharToCurrObj")
    Currency dtCurrencyCharToCurrencyObj(char[] currencyCode){
        return Currency.getInstance(new String(currencyCode));
    }

    @Named("daoDTIntoDtoDT")
    OffsetDateTime daoDTIntoDtoDT(DebitTransaction dt){
        return OffsetDateTime.from(dt.getTrDateTime()
                .atOffset(ZoneOffset.ofTotalSeconds(dt.getTimeZoneSecOffset())));
    }

    //Ставим UTC, потому что при сохранении нового лимита сервис сохраняет дату сохранения в UTC
    @Named("utcDtToOffDt")
    OffsetDateTime utcDtToOffDt(LocalDateTime limitUtcDt){
        return limitUtcDt.atOffset(ZoneOffset.UTC);
    }



}
