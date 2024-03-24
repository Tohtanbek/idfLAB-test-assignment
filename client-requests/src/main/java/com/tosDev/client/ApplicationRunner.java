package com.tosDev.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.tosDev.client",
        "com.tosDev.tr.spring.jpa",
        "com.tosDev.tr.enums"})
@ComponentScan("com.tosDev.tr.spring.jpa")
@EnableJpaRepositories(basePackages = "com.tosDev.tr.spring.jpa.repository")
@EntityScan("com.tosDev.tr.spring.jpa.*")
public class ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(com.tosDev.client.ApplicationRunner.class,args);
    }
}
