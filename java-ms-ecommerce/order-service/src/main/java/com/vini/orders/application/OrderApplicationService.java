package com.vini.orders.application;

import com.vini.orders.domain.exception.OrderNotFoundException;
import com.vini.orders.domain.exception.PaymentDeclinedException;
import com.vini.orders.domain.model.*;
import com.vini.orders.dto.CreateOrderRequest;
import com.vini.orders.infrastructure.persistence.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class OrderApplicationService {

  private final OrderRepository repo;
  private final InventoryClient inventory;
  private final PaymentClient payment;
  private final ObjectProvider<OrderApplicationService> selfProvider;

  public OrderApplicationService(
      OrderRepository repo,
      InventoryClient inventory,
      PaymentClient payment,
      ObjectProvider<OrderApplicationService> selfProvider
  ) {
    this.repo = repo;
    this.inventory = inventory;
    this.payment = payment;
    this.selfProvider = selfProvider;
  }

  @Transactional
  public Order create(CreateOrderRequest req) {
    var order = newOrder(req);
    order = repo.save(order);
    return processPaymentFlow(order.getId());
  }

  @Transactional
  public Order processPaymentFlow(UUID orderId) {
    var order = getOrderOrThrow(orderId);

    if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CANCELED) {
      return order;
    }

    return selfProvider.getObject().payWithResilience(orderId);
  }

  @Retry(name = "payment")
  @CircuitBreaker(name = "payment", fallbackMethod = "paymentFallback")
  @Transactional
  public Order payWithResilience(UUID orderId) {
    var order = getOrderOrThrow(orderId);

    var items = order.getItems().stream()
        .map(it -> new InventoryClient.Item(it.getSku(), it.getQuantity()))
        .toList();

    inventory.reserve(order.getId(), items);

    var amount = BigDecimal.valueOf(order.totalItems()).multiply(BigDecimal.TEN);
    try {
      payment.pay(order.getId(), amount);
      order.setStatus(OrderStatus.PAID);
      return repo.save(order);
    } catch (PaymentDeclinedException ex) {
      try { inventory.release(order.getId()); } catch (Exception ignored) {}
      order.setStatus(OrderStatus.CANCELED);
      return repo.save(order);
    }
  }

  @Transactional
  public Order paymentFallback(UUID orderId, Throwable t) {
    var order = getOrderOrThrow(orderId);

    try { inventory.release(order.getId()); } catch (Exception ignored) {}

    order.setStatus(OrderStatus.CANCELED);
    return repo.save(order);
  }

  @Transactional(readOnly = true)
  public Order get(UUID id) {
    return getOrderOrThrow(id);
  }

  private Order newOrder(CreateOrderRequest req) {
    var order = new Order(req.customerId(), OrderStatus.CREATED, Instant.now());
    req.items().forEach(i -> order.addItem(new OrderItem(i.sku(), i.quantity())));
    return order;
  }

  private Order getOrderOrThrow(UUID id) {
    return repo.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
  }
}
