package com.tosDev.tr.util.mapstruct;

import com.tosDev.tr.dto.ExchangeRateDto;
import com.tosDev.tr.spring.jpa.entity.ExchangeRate;
import com.tosDev.tr.dto.DebitTrDto;
import com.tosDev.tr.spring.jpa.entity.DebitTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
@Mapper(componentModel = "spring")
public abstract class MapStructMapper {

    @Autowired
    @Qualifier(value = "dateTimeFormatter")
    DateTimeFormatter dateTimeFormatter;


    //Мапперы объектов траназакций
    @Mapping(source = "currency",target = "currencyCode",qualifiedByName = "dtoCurrencyToDaoCurrency")
    @Mapping(source = "dateTime",target = "trDateTime",qualifiedByName = "offsetDateTimeToTimeStamp")
    @Mapping(source = "dateTime",target = "timeZoneSecOffset",qualifiedByName = "offsetDateTimeToOffset")
    public abstract DebitTransaction debitDtoToDebitDao(DebitTrDto debitTrDto);
    @Mapping(source = "currencyCode",target = "currency",qualifiedByName = "daoCurrencyToDtoCurrency")
    @Mapping(source = ".", target = "dateTime",qualifiedByName = "timeStampAndOffsetToOffsetDateTime")
    public abstract DebitTrDto debitDaoToDebitDto(DebitTransaction debitDao);


    @Named("daoCurrencyToDtoCurrency")
    Currency daoCurrencyToDtoCurrency(char[] currencyCode){
        return Currency.getInstance(String.valueOf(currencyCode));
    }

    @Named("dtoCurrencyToDaoCurrency")
    char[] dtoCurrencyToDaoCurrency(Currency currency){
        return currency.getCurrencyCode().toCharArray();
    }

    //Сначала переносим локальное время транзакции
    @Named("offsetDateTimeToTimeStamp")
    LocalDateTime dtoDateTimeToDaoDateTime(OffsetDateTime dtoDateTime){
        return dtoDateTime.toLocalDateTime();
    }
    //Потом переносим часовой пояс (сдвиг в секундах)
    @Named("offsetDateTimeToOffset")
    Integer dtoDateTimeToDaoOffset(OffsetDateTime dtoDateTime){
        return dtoDateTime.getOffset().getTotalSeconds();
    }

    //Перенос timestamp и offset из базы данных в dto
    @Named("timeStampAndOffsetToOffsetDateTime")
    OffsetDateTime daoDateTimeToDtoDateTime(DebitTransaction debitTransaction){
         return OffsetDateTime.from(debitTransaction.getTrDateTime()
                 .atOffset(ZoneOffset.ofTotalSeconds(debitTransaction.getTimeZoneSecOffset())));
    }

    //_________
    //Мапперы объектов курса валют

    @Mapping(source = "currency",target = "currencyCode",qualifiedByName = "currencyObjToCharArrCode")
    @Mapping(source = "accessDateTime", target = "accessDateTime", qualifiedByName = "offsetDateTimeToLocalDateTime")
    public abstract ExchangeRate exchangeRateDtoToDao(ExchangeRateDto exchangeRateDto);

    @Named("currencyObjToCharArrCode")
    char[] currencyObjToCharArrCode(Currency currency){
        return currency.getCurrencyCode().toCharArray();
    }

    @Named("offsetDateTimeToLocalDateTime")
    LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toLocalDateTime();
    }



}
