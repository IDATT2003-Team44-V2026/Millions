package edu.ntnu.idi.idatt2003.model;

import java.math.BigDecimal;

/**
 * Represents a share holding, i.e. a specific quantity of a {@link Stock} acquired at a given
 * purchase price.
 *
 * <p>A share is immutable once created; the stock, quantity, and purchase price
 * cannot be changed after construction.</p>
 */
public record Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {

  /**
   * Creates a new share holding.
   *
   * @param stock         the stock that this share refers to; must not be {@code null}
   * @param quantity      the number of shares held; must not be {@code null} and must be positive
   * @param purchasePrice the price per share at the time of purchase; must not be {@code null} and
   *                      must be positive
   * @throws IllegalArgumentException if {@code stock} is {@code null}, if {@code quantity} is
   *                                  {@code null} or not positive, or if {@code purchasePrice} is
   *                                  {@code null} or not positive
   */
  public Share {
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

  }

  /**
   * Returns the stock that this share refers to.
   *
   * @return the underlying {@link Stock}
   */
  @Override
  public Stock stock() {
    return stock;
  }

  /**
   * Returns the number of shares held.
   *
   * @return the quantity
   */
  @Override
  public BigDecimal quantity() {
    return quantity;
  }

  /**
   * Returns the price per share at the time of purchase.
   *
   * @return the purchase price
   */
  @Override
  public BigDecimal purchasePrice() {
    return purchasePrice;
  }
}
