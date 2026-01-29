package com.vini.inventory.application;

import com.vini.inventory.domain.Reservation;
import com.vini.inventory.infrastructure.ReservationRepository;
import com.vini.inventory.infrastructure.StockItemRepository;
import com.vini.inventory.dto.ReserveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InventoryService {

  private final StockItemRepository stockRepo;
  private final ReservationRepository reservationRepo;

  @Transactional
  public void reserve(ReserveRequest req) {
    if (req.orderId() == null) throw new IllegalArgumentException("orderId is required");

    // idempotencia: se ja reservou esse orderId, nao reserva de novo
    var existing = reservationRepo.findByOrderId(req.orderId());
    if (!existing.isEmpty()) return;

    for (var it : req.items()) {
      var stock = stockRepo.findBySkuForUpdate(it.sku())
          .orElseThrow(() -> new IllegalStateException("sku not found: " + it.sku()));

      stock.reserve(it.quantity());
      stockRepo.save(stock);

      reservationRepo.save(Reservation.builder()
          .orderId(req.orderId())
          .sku(it.sku())
          .quantity(it.quantity())
          .createdAt(Instant.now())
          .build());
    }
  }

  @Transactional
  public void release(java.util.UUID orderId) {
    if (orderId == null) throw new IllegalArgumentException("orderId is required");

    var reservations = reservationRepo.findByOrderId(orderId);
    if (reservations.isEmpty()) return; // idempotente

    for (var r : reservations) {
      var stock = stockRepo.findBySkuForUpdate(r.getSku())
          .orElseThrow(() -> new IllegalStateException("sku not found: " + r.getSku()));

      stock.release(r.getQuantity());
      stockRepo.save(stock);
    }

    reservationRepo.deleteByOrderId(orderId);
  }

  @Transactional(readOnly = true)
  public com.vini.inventory.domain.StockItem getStock(String sku) {
    return stockRepo.findBySku(sku)
        .orElseThrow(() -> new IllegalStateException("sku not found: " + sku));
  }
}
