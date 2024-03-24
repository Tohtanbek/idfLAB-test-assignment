package com.tosDev.tr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tosDev.tr.util.exchange.DailyExchangeLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApplicationRunner {
    public static void main(String[] args) throws JsonProcessingException {
        var x = SpringApplication.run(ApplicationRunner.class,args);
        DailyExchangeLoader y = x.getBean(DailyExchangeLoader.class);
        y.loadTodayRates();
    }
}
