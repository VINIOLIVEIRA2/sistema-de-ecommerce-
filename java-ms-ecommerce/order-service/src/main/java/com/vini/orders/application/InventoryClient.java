package com.vini.orders.application;

import java.util.UUID;
import java.util.List;

public interface InventoryClient {
  void reserve(UUID orderId, List<Item> items);
  void release(UUID orderId);

  record Item(String sku, int quantity) {}
}
