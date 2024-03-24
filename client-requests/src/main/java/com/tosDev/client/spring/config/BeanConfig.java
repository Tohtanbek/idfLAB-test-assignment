package com.tosDev.client.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
    public ObjectMapper objectMapper(){
        return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }
}
