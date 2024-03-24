package com.tosDev.util.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tosDev.dto.ExchangeRateDto;
import com.tosDev.spring.rest.feign.ExchangeClient;
import com.tosDev.spring.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Component
public class DailyExchangeLoader {
    private final ExchangeClient exchangeClient;
    private final ObjectMapper objectMapper;
    @Qualifier("apiDateFormatter")
    private final DateTimeFormatter apiDateFormatter;
    private final ExchangeService exchangeService;

    /**
     * Вызывает обращение к стороннему в полночь по будням
     * Парсим и отправляем на сохранение в @Service
     * Ожидает body с json котировок всех доступных валют
     */
    @Scheduled(cron = "0 0 0 * * MON-FRI")
    public void loadTodayRates() throws JsonProcessingException {
        ResponseEntity<String> rates =
                exchangeClient.loadActualRates();
        if (rates.getStatusCode().is2xxSuccessful()) {
            log.info("Ежедневное обновление курсов валют");
            log.trace("Загруженные курсы: {}", rates.getBody());

            JsonNode rootNode = objectMapper.readTree(rates.getBody());
            List<ExchangeRateDto> exchangeRateDtoList = mapJsonNodeToDtoList(rootNode);
            exchangeService.saveExchangeRateDto(exchangeRateDtoList);
        }
        else {
            log.error("Ошибка стороннего api при ежедневной загрузке валют {} {}",
                    rates.getStatusCode(), rates.getBody());
            //Далее обработка ошибки по заданной бизнес-логике
        }
    }


    /**
     * Маппер для полученного json с курсами валют
     * @param rootNode Нода полученного json
     * @return Список дто для дальнейшей работы с информацией и последующего сохранения в бд
     */
    List<ExchangeRateDto> mapJsonNodeToDtoList(JsonNode rootNode){
        List<ExchangeRateDto> exchangeRateDtoList = new ArrayList<>();

        JsonNode neededNode = rootNode.get("Valute");
        String dateStr = rootNode.get("Date").textValue();
        OffsetDateTime generalDateTime = OffsetDateTime.parse(dateStr,apiDateFormatter);
        var iterator = neededNode.fields();
        while (iterator.hasNext()){
            Map.Entry<String,JsonNode> jsonObject = iterator.next();
            try {
                Currency currency = Currency.getInstance(jsonObject.getKey());
                JsonNode details = jsonObject.getValue();
                double nominal = details.get("Nominal").asDouble();
                double rate = details.get("Value").doubleValue();
                rate = rate/nominal;
                exchangeRateDtoList.add(new ExchangeRateDto(currency,rate,generalDateTime));
            } catch (IllegalArgumentException e) {
                log.error("api отправил некорректную валюту");
                e.printStackTrace();
            }
        }
        return exchangeRateDtoList;
    }

}
