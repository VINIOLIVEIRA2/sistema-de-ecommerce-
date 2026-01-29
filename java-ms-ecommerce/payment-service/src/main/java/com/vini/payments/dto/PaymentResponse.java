package com.vini.payments.dto;

import com.vini.payments.domain.PaymentStatus;

import java.util.UUID;

public record PaymentResponse(
    UUID id,
    UUID orderId,
    PaymentStatus status
) {}
