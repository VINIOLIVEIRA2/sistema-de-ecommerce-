package com.vini.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReserveRequest(
    UUID orderId,
    @NotEmpty List<@Valid Item> items
) {
  public record Item(@NotBlank String sku, @Min(1) int quantity) {}
}
