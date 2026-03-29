package com.portal.jira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JiraSyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(JiraSyncApplication.class, args);
    }
}