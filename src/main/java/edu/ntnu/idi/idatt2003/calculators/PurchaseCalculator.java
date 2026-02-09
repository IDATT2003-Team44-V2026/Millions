package edu.ntnu.idi.idatt2003.calculators;

import edu.ntnu.idi.idatt2003.model.Share;
import java.math.BigDecimal;

/**
 * Calculator for computing the financial values and costs of a purchase transaction.
 *
 * <p>Calculations are based on the share's purchase price and quantity.
 * The following rules apply:</p>
 * <ul>
 *   <li><b>Gross value:</b> purchase price &times; quantity</li>
 *   <li><b>Commission:</b> 0.5&nbsp;% of gross value</li>
 *   <li><b>Tax:</b> no tax on purchases (always zero)</li>
 *   <li><b>Total:</b> gross + commission + tax</li>
 * </ul>
 */
public class PurchaseCalculator implements TransactionCalculator {
    private final BigDecimal purchasePrice;
    private final BigDecimal quantity;

    /**
     * Creates a new purchase calculator for the given share.
     *
     * <p>The calculator extracts the purchase price and quantity from the share.</p>
     *
     * @param share the share being purchased; must not be {@code null}
     * @throws IllegalArgumentException if {@code share} is {@code null}
     */
    public PurchaseCalculator(Share share) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        
        this.purchasePrice = share.getPurchasePrice();
        this.quantity = share.getQuantity();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Calculated as purchase price &times; quantity.</p>
     */
    @Override
    public BigDecimal calculateGross() {
        return purchasePrice.multiply(quantity);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Calculated as 0.5&nbsp;% of gross value.</p>
     */
    @Override
    public BigDecimal calculateCommission() {
        return calculateGross().multiply(new BigDecimal("0.005"));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Always returns zero; no tax is charged on purchases.</p>
     */
    @Override
    public BigDecimal calculateTax() {
        return BigDecimal.ZERO;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Calculated as gross + commission + tax.</p>
     */
    @Override
    public BigDecimal calculateTotal() {
        return calculateGross().add(calculateCommission()).add(calculateTax());
    }
}
