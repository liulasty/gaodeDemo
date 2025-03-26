package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AMapConfig {

    @Value("${amap.api.key}")
    private String apiKey;

    @Bean
    public WebClient amapWebClient() {
        return WebClient.builder()
                .baseUrl("https://restapi.amap.com")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    public String amapApiKey() {
        return apiKey;
    }
}