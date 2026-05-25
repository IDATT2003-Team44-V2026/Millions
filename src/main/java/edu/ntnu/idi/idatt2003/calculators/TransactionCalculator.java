package edu.ntnu.idi.idatt2003.calculators;

import java.math.BigDecimal;

/**
 * Defines the contract for calculating the financial values and costs associated with a
 * transaction.
 *
 * <p>Implementations determine how gross value, commission, tax, and total
 * value are computed for specific transaction types such as purchases and sales.</p>
 *
 * @see edu.ntnu.idi.idatt2003.calculators.PurchaseCalculator
 * @see edu.ntnu.idi.idatt2003.calculators.SaleCalculator
 */
public interface TransactionCalculator {

  /**
   * Calculates the gross value of the transaction before any fees or taxes.
   *
   * @return the gross value
   */
  BigDecimal calculateGross();

  /**
   * Calculates the commission (brokerage fee) for the transaction.
   *
   * @return the commission amount
   */
  BigDecimal calculateCommission();

  /**
   * Calculates the tax owed on the transaction.
   *
   * @return the tax amount
   */
  BigDecimal calculateTax();

  /**
   * Calculates the total value of the transaction after fees and taxes.
   *
   * @return the total value
   */
  BigDecimal calculateTotal();
}
