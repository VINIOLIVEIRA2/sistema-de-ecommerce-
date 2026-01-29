package com.vini.inventory.infrastructure;

import com.vini.inventory.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select s from StockItem s where s.sku = :sku")
  Optional<StockItem> findBySkuForUpdate(@Param("sku") String sku);

  Optional<StockItem> findBySku(String sku);
}
