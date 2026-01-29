package com.vini.orders.infrastructure.http;

import com.vini.orders.application.PaymentClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.UUID;

import com.vini.orders.domain.exception.PaymentDeclinedException;

@Component
public class PaymentHttpClient implements PaymentClient {

  private final RestClient.Builder builder;

  @Value("${clients.payment.base-url}")
  private String baseUrl;

  public PaymentHttpClient(RestClient.Builder builder) {
    this.builder = builder;
  }

  @Override
  public void pay(UUID orderId, BigDecimal amount) {
    var client = builder.baseUrl(baseUrl).build();
    try {
      client.post()
          .uri("/payments")
          .body(new PaymentRequest(orderId, amount))
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientResponseException ex) {
      if (ex.getRawStatusCode() == 402) {
        throw new PaymentDeclinedException(orderId);
      }
      throw new RuntimeException("Payment failed: " + ex.getStatusCode(), ex);
    }
  }

  record PaymentRequest(UUID orderId, BigDecimal amount) {}
}
