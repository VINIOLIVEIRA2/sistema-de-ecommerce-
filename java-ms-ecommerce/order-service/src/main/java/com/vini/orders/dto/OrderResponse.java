package com.vini.orders.dto;

import com.vini.orders.domain.model.OrderStatus;

import java.util.UUID;

public record OrderResponse(
    UUID id,
    OrderStatus status,
    int totalItems
) {}
