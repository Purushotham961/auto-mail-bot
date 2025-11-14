package org.purushotham;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MarketIntelligenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketIntelligenceApplication.class, args);
    }
}
