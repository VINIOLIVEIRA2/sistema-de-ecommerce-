package com.vini.orders.application;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentClient {
  void pay(UUID orderId, BigDecimal amount);
}
