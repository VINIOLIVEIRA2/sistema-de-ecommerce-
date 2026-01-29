package com.vini.payments.application;

import com.vini.payments.domain.Payment;
import com.vini.payments.domain.PaymentStatus;
import com.vini.payments.dto.CreatePaymentRequest;
import com.vini.payments.infrastructure.FailureSimulator;
import com.vini.payments.infrastructure.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository repo;
  private final FailureSimulator failure;

  @Transactional
  public Payment pay(CreatePaymentRequest req) {
    // idempotencia: se ja pagou/recusou esse orderId, retorna o existente
    var existing = repo.findByOrderId(req.orderId());
    if (existing.isPresent()) return existing.get();

    var status = failure.shouldFail() ? PaymentStatus.DECLINED : PaymentStatus.APPROVED;

    var payment = Payment.builder()
        .orderId(req.orderId())
        .amount(req.amount())
        .status(status)
        .createdAt(Instant.now())
        .build();

    return repo.save(payment);
  }
}
