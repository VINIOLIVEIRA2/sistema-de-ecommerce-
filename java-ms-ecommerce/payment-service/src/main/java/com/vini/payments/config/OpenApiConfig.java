package com.vini.payments.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  OpenAPI openAPI() {
    return new OpenAPI().info(new Info()
        .title("Payment Service API")
        .description("Payments: simulate approve/decline (20% failure)")
        .version("v1"));
  }
}
