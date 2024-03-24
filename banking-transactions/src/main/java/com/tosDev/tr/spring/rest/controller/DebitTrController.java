package com.tosDev.tr.spring.rest.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tosDev.tr.spring.service.DebitTrService;
import com.tosDev.tr.dto.DebitTrDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/debit-tr")
@Slf4j
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DebitTrController {

    private final DebitTrService debitTrService;

    /**
     * Принимает json поступившей транзакции из банковского api
     * и направляет на сервис для маппинга и сохранения транзакции в бд
     * @param debitTrJson json body транзакции
     */
    @PostMapping("/receive-tr")
    ResponseEntity<Void> receiveTr(@RequestBody DebitTrDto debitTrJson){
        log.info("post request от banking api на запись транзакции {}",debitTrJson);
        return debitTrService.mapAndSaveTr(debitTrJson);
    }
}
