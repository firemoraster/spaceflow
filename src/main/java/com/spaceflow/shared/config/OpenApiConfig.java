package com.spaceflow.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI spaceFlowOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SpaceFlow API")
                .version("v1")
                .description("Event-driven space booking platform")
                .license(new License().name("MIT")));
    }
}
