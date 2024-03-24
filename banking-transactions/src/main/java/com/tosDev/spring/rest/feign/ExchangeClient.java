package com.tosDev.spring.rest.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "openExchangeRates", url = "https://www.cbr-xml-daily.ru")
public interface ExchangeClient {
    @RequestMapping(method = RequestMethod.GET, value = "/daily_json.js")
    ResponseEntity<String> loadActualRates();
}
