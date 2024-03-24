package com.tosDev.client.spring.rest.controller;

import com.tosDev.client.spring.service.ClientLimitsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/client-limits")
public class ClientLimitController {

    private final ClientLimitsService clientLimitsService;

    /**
     * Запрос на новый актуальный лимит
     * @param ordinalCategory одна из двух категорий транзакций
     * @param freshLimit сумма нового лимита
     * @return 200/500
     */
    @PostMapping("/set-fresh-limit")
    ResponseEntity<Void> setFreshLimit(@RequestParam(name = "category") Integer ordinalCategory,
                                         @RequestParam(name = "limit") Double freshLimit) {
        log.info("Запрос на обновление лимита категории {} на новое значение {} от клиента",
                ordinalCategory,freshLimit);
        return clientLimitsService.setFreshLimit(ordinalCategory,freshLimit);
    }

    /**
     * Запрос на все отображение всех транзакций, превысивших лимит
     * вместе с информацией о превышенных лимитах
     * @return 200 и json список dto / 500
     */
    @GetMapping("/show-exceed-transactions")
    ResponseEntity<String> showExceedTransactions() {
        log.info("Запрос на список транзакций, превысивших лимит от клиента");
        return clientLimitsService.showExceedRows();
    }

}
