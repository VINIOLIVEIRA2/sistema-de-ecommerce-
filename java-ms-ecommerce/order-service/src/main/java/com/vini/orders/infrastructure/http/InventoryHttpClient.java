package com.vini.orders.infrastructure.http;

import com.vini.orders.application.InventoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
public class InventoryHttpClient implements InventoryClient {

  private final RestClient.Builder builder;

  @Value("${clients.inventory.base-url}")
  private String baseUrl;

  public InventoryHttpClient(RestClient.Builder builder) {
    this.builder = builder;
  }

  @Override
  public void reserve(UUID orderId, List<Item> items) {
    var client = builder.baseUrl(baseUrl).build();
    client.post()
        .uri("/inventory/reserve")
        .body(new ReserveRequest(orderId, items))
        .retrieve()
        .onStatus(HttpStatusCode::isError, (req, res) -> {
          throw new RuntimeException("Inventory reserve failed: " + res.getStatusCode());
        })
        .toBodilessEntity();
  }

  @Override
  public void release(UUID orderId) {
    var client = builder.baseUrl(baseUrl).build();
    client.post()
        .uri("/inventory/release")
        .body(new ReleaseRequest(orderId))
        .retrieve()
        .toBodilessEntity();
  }

  record ReserveRequest(UUID orderId, List<Item> items) {}
  record ReleaseRequest(UUID orderId) {}
}
