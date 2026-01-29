package com.vini.orders.controller;

import com.vini.orders.application.OrderApplicationService;
import com.vini.orders.dto.CreateOrderRequest;
import com.vini.orders.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderApplicationService service;

  public OrderController(OrderApplicationService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponse create(@RequestBody @Valid CreateOrderRequest req) {
    var o = service.create(req);
    return new OrderResponse(o.getId(), o.getStatus(), o.totalItems());
  }

  @GetMapping("/{id}")
  public OrderResponse get(@PathVariable UUID id) {
    var o = service.get(id);
    return new OrderResponse(o.getId(), o.getStatus(), o.totalItems());
  }

  @PostMapping("/{id}/process")
  public OrderResponse process(@PathVariable UUID id) {
    var o = service.processPaymentFlow(id);
    return new OrderResponse(o.getId(), o.getStatus(), o.totalItems());
  }
}
