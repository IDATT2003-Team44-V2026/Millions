package edu.ntnu.idi.idatt2003.model;

import java.math.BigDecimal;

public class Share {
  private final Stock stock;
  private final BigDecimal quantity;
  private final BigDecimal purchasePrice;

  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
    if (stock == null) {
      throw new IllegalArgumentException("Stock cannot be null");
    }
    if (quantity == null) {
      throw new IllegalArgumentException("Quantity cannot be null");
    }
    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (purchasePrice == null) {
      throw new IllegalArgumentException("Purchase price cannot be null");
    }
    if (purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Purchase price must be positive");
    }
    
    this.stock = stock;
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
  }

  public Stock getStock() {
    return this.stock;
  }

  public BigDecimal getQuantity() {
    return this.quantity;
  }

  public BigDecimal getPurchasePrice() {
    return this.purchasePrice;
  }
}