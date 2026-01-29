package com.vini.inventory.config;

import com.vini.inventory.domain.StockItem;
import com.vini.inventory.infrastructure.StockItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SeedData {

  private final StockItemRepository repo;

  @Bean
  CommandLineRunner seed() {
    return args -> {
      if (repo.findBySku("SKU-1").isEmpty()) {
        repo.save(StockItem.builder().sku("SKU-1").availableQuantity(100).build());
        repo.save(StockItem.builder().sku("SKU-2").availableQuantity(50).build());
        repo.save(StockItem.builder().sku("SKU-3").availableQuantity(10).build());
      }
    };
  }
}
