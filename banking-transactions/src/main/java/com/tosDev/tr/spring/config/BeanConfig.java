package com.tosDev.tr.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class BeanConfig {
    @Bean
    DateTimeFormatter dateTimeFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX");
    }

    @Bean
    DateTimeFormatter apiDateFormatter(){
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }
}
