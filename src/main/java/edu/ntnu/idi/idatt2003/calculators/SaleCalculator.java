package edu.ntnu.idi.idatt2003.calculators;

import edu.ntnu.idi.idatt2003.model.Share;
import java.math.BigDecimal;

/**
 * Calculator for computing the financial values and costs of a sale transaction.
 *
 * <p>Calculations are based on the share's current sales price, purchase price,
 * and quantity. The following rules apply:</p>
 * <ul>
 *   <li><b>Gross value:</b> sales price &times; quantity</li>
 *   <li><b>Commission:</b> 1&nbsp;% of gross value</li>
 *   <li><b>Tax:</b> 30&nbsp;% of profit, where profit = gross &minus; commission
 *       &minus; purchase costs (purchase price &times; quantity). No tax if profit
 *       is zero or negative.</li>
 *   <li><b>Total:</b> gross &minus; commission &minus; tax</li>
 * </ul>
 */
public class SaleCalculator implements TransactionCalculator {

  private final BigDecimal purchasePrice;
  private final BigDecimal salesPrice;
  private final BigDecimal quantity;

  /**
   * Creates a new sale calculator for the given share.
   *
   * <p>The calculator extracts the purchase price, current sales price, and
   * quantity from the share.</p>
   *
   * @param share the share being sold; must not be {@code null}
   * @throws IllegalArgumentException if {@code share} is {@code null}
   */
  public SaleCalculator(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }

    this.purchasePrice = share.purchasePrice();
    this.salesPrice = share.stock().getSalesPrice();
    this.quantity = share.quantity();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Calculated as sales price &times; quantity.</p>
   */
  @Override
  public BigDecimal calculateGross() {
    return salesPrice.multiply(quantity);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Calculated as 1&nbsp;% of gross value.</p>
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross().multiply(new BigDecimal("0.01"));
  }

  /**
   * {@inheritDoc}
   *
   * <p>Calculated as 30&nbsp;% of profit. Profit is defined as
   * gross &minus; commission &minus; purchase costs (purchase price &times; quantity). Returns zero
   * if the profit is zero or negative.</p>
   */
  @Override
  public BigDecimal calculateTax() {
    BigDecimal gross = calculateGross();
    BigDecimal commission = calculateCommission();
    BigDecimal purchaseCosts = purchasePrice.multiply(quantity);
    BigDecimal profit = gross.subtract(commission).subtract(purchaseCosts);

    if (profit.compareTo(BigDecimal.ZERO) <= 0) {
      return BigDecimal.ZERO;
    }

    return profit.multiply(new BigDecimal("0.30"));
  }

  /**
   * {@inheritDoc}
   *
   * <p>Calculated as gross &minus; commission &minus; tax.</p>
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
  }
}
