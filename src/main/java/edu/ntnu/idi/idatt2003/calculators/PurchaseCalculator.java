package edu.ntnu.idi.idatt2003.calculators;

import edu.ntnu.idi.idatt2003.model.Share;
import java.math.BigDecimal;

public class PurchaseCalculator implements TransactionCalculator {
    private final BigDecimal purchasePrice;
    private final BigDecimal quantity;

    public PurchaseCalculator(Share share) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        
        this.purchasePrice = share.getPurchasePrice();
        this.quantity = share.getQuantity();
    }

    @Override
    public BigDecimal calculateGross() {
        return purchasePrice.multiply(quantity);
    }

    @Override
    public BigDecimal calculateCommission() {
        return calculateGross().multiply(new BigDecimal("0.005"));
    }

    @Override
    public BigDecimal calculateTax() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotal() {
        return calculateGross().add(calculateCommission()).add(calculateTax());
    }
}
