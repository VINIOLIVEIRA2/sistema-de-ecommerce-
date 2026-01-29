package com.vini.payments.controller;

import com.vini.payments.application.PaymentService;
import com.vini.payments.dto.CreatePaymentRequest;
import com.vini.payments.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

  private final PaymentService service;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public PaymentResponse pay(@RequestBody @Valid CreatePaymentRequest req) {
    var p = service.pay(req);

    // Se declined, devolve 402 (pra forcar Order a tratar falha)
    if (p.getStatus().name().equals("DECLINED")) {
      throw new PaymentDeclinedException("Payment declined for orderId=" + p.getOrderId());
    }

    return new PaymentResponse(p.getId(), p.getOrderId(), p.getStatus());
  }

  @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
  static class PaymentDeclinedException extends RuntimeException {
    PaymentDeclinedException(String msg) { super(msg); }
  }
}
