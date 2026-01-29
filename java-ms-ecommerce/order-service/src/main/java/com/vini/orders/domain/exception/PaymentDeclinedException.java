package com.vini.orders.domain.exception;

import java.util.UUID;

public class PaymentDeclinedException extends RuntimeException {
  public PaymentDeclinedException(UUID orderId) {
    super("Payment declined for order: " + orderId);
  }
}
