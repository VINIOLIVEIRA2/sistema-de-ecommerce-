package com.vini.inventory.dto;

import java.util.UUID;

public record ReserveResponse(UUID orderId, String status) {}
