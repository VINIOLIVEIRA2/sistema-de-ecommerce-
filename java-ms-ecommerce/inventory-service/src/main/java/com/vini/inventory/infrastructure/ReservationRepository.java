package com.vini.inventory.infrastructure;

import com.vini.inventory.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  List<Reservation> findByOrderId(UUID orderId);
  void deleteByOrderId(UUID orderId);
}
