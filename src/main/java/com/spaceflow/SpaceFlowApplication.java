package com.spaceflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SpaceFlow — event-driven space booking platform.
 *
 * <p>Modular monolith: each top-level package ({@code booking}, {@code resource},
 * {@code notification}) is an independent module following hexagonal architecture.
 * Modules communicate only via domain events (Kafka) or published ports —
 * never by reaching into another module's persistence layer.
 */
@SpringBootApplication
@EnableCaching
@EnableKafka
@EnableAsync
@EnableScheduling
public class SpaceFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaceFlowApplication.class, args);
    }
}
