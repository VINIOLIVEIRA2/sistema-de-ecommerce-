package com.vini.inventory.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_items", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String sku;

  @Column(nullable = false)
  private int availableQuantity;

  public void reserve(int qty) {
    if (qty <= 0) throw new IllegalArgumentException("quantity must be >= 1");
    if (availableQuantity < qty) throw new IllegalStateException("insufficient stock for sku=" + sku);
    availableQuantity -= qty;
  }

  public void release(int qty) {
    if (qty <= 0) throw new IllegalArgumentException("quantity must be >= 1");
    availableQuantity += qty;
  }
}
