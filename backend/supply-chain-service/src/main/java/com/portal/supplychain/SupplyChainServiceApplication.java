package com.portal.supplychain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableKafka
@EnableAsync
public class SupplyChainServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupplyChainServiceApplication.class, args);
    }
}