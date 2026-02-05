package edu.ntnu.idi.idatt2003.calculators;

import edu.ntnu.idi.idatt2003.model.Share;
import java.math.BigDecimal;

public class SaleCalculator implements TransactionCalculator {
    private final BigDecimal purchasePrice;
    private final BigDecimal salesPrice;
    private final BigDecimal quantity;

    public SaleCalculator(Share share) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        
        this.purchasePrice = share.getPurchasePrice();
        this.salesPrice = share.getStock().getSalesPrice();
        this.quantity = share.getQuantity();
    }

    @Override
    public BigDecimal calculateGross() {
        return salesPrice.multiply(quantity);
    }

    @Override
    public BigDecimal calculateCommission() {
        return calculateGross().multiply(new BigDecimal("0.01"));
    }

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

    @Override
    public BigDecimal calculateTotal() {
        return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
    }
}
