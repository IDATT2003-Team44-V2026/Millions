package edu.ntnu.idi.idatt2003.calculators;

import java.math.BigDecimal;

public interface TransactionCalculator {
    BigDecimal calculateGross();
    
    BigDecimal calculateCommission();
    
    BigDecimal calculateTax();
    
    BigDecimal calculateTotal();
}
