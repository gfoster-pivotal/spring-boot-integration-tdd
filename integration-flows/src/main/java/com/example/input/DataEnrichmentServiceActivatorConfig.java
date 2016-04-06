package com.example.input;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DataEnrichmentServiceActivatorConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
