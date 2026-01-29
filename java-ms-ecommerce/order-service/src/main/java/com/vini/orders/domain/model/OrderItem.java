package com.vini.orders.domain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String sku;

  @Column(nullable = false)
  private int quantity;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  protected OrderItem() { }

  public OrderItem(String sku, int quantity) {
    this.sku = sku;
    this.quantity = quantity;
  }

  public UUID getId() {
    return id;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }
}
