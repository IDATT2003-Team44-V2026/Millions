package edu.ntnu.idi.idatt2003.calculators;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Purchase Calculator Tests")
class PurchaseCalculatorTest {
    private Stock stock;
    private Share share;
    private PurchaseCalculator calculator;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
        calculator = new PurchaseCalculator(share);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create calculator with valid share")
        void shouldCreateCalculatorWithValidShare() {
            PurchaseCalculator newCalculator = new PurchaseCalculator(share);

            assertNotNull(newCalculator);
        }

        @Test
        @DisplayName("Should throw exception when share is null")
        void shouldThrowExceptionWhenShareIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new PurchaseCalculator(null)
            );
        }
    }

    @Nested
    @DisplayName("Calculate Gross Tests")
    class CalculateGrossTests {

        @Test
        @DisplayName("Should calculate gross as purchase price times quantity")
        void shouldCalculateGrossAsPurchasePriceTimesQuantity() {
            BigDecimal gross = calculator.calculateGross();

            assertEquals(0, gross.compareTo(new BigDecimal("1000.00")));
        }

        @Test
        @DisplayName("Should calculate gross for fractional quantity")
        void shouldCalculateGrossForFractionalQuantity() {
            Share fractionalShare = new Share(stock, new BigDecimal("2.5"), new BigDecimal("100.00"));
            PurchaseCalculator fractionalCalculator = new PurchaseCalculator(fractionalShare);

            BigDecimal gross = fractionalCalculator.calculateGross();

            assertEquals(0, gross.compareTo(new BigDecimal("250.00")));
        }

        @Test
        @DisplayName("Should calculate gross for fractional price")
        void shouldCalculateGrossForFractionalPrice() {
            Share fractionalPriceShare = new Share(stock, new BigDecimal("10"), new BigDecimal("99.99"));
            PurchaseCalculator fractionalPriceCalculator = new PurchaseCalculator(fractionalPriceShare);

            BigDecimal gross = fractionalPriceCalculator.calculateGross();

            assertEquals(0, gross.compareTo(new BigDecimal("999.90")));
        }

        @Test
        @DisplayName("Should calculate gross for single share")
        void shouldCalculateGrossForSingleShare() {
            Share singleShare = new Share(stock, new BigDecimal("1"), new BigDecimal("100.00"));
            PurchaseCalculator singleCalculator = new PurchaseCalculator(singleShare);

            BigDecimal gross = singleCalculator.calculateGross();

            assertEquals(0, gross.compareTo(new BigDecimal("100.00")));
        }
    }

    @Nested
    @DisplayName("Calculate Commission Tests")
    class CalculateCommissionTests {

        @Test
        @DisplayName("Should calculate commission as 0.5% of gross")
        void shouldCalculateCommissionAsHalfPercentOfGross() {
            BigDecimal commission = calculator.calculateCommission();

            assertEquals(0, commission.compareTo(new BigDecimal("5.00")));
        }

        @Test
        @DisplayName("Should calculate commission correctly for small amounts")
        void shouldCalculateCommissionCorrectlyForSmallAmounts() {
            Share smallShare = new Share(stock, new BigDecimal("1"), new BigDecimal("10.00"));
            PurchaseCalculator smallCalculator = new PurchaseCalculator(smallShare);

            BigDecimal commission = smallCalculator.calculateCommission();

            assertEquals(0, commission.compareTo(new BigDecimal("0.05")));
        }

        @Test
        @DisplayName("Should calculate commission correctly for large amounts")
        void shouldCalculateCommissionCorrectlyForLargeAmounts() {
            Share largeShare = new Share(stock, new BigDecimal("1000"), new BigDecimal("1000.00"));
            PurchaseCalculator largeCalculator = new PurchaseCalculator(largeShare);

            BigDecimal commission = largeCalculator.calculateCommission();

            assertEquals(0, commission.compareTo(new BigDecimal("5000.00")));
        }
    }

    @Nested
    @DisplayName("Calculate Tax Tests")
    class CalculateTaxTests {

        @Test
        @DisplayName("Should return zero tax for purchase")
        void shouldReturnZeroTaxForPurchase() {
            BigDecimal tax = calculator.calculateTax();

            assertEquals(0, tax.compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("Should always return zero tax regardless of amount")
        void shouldAlwaysReturnZeroTaxRegardlessOfAmount() {
            Share largeShare = new Share(stock, new BigDecimal("1000"), new BigDecimal("5000.00"));
            PurchaseCalculator largeCalculator = new PurchaseCalculator(largeShare);

            BigDecimal tax = largeCalculator.calculateTax();

            assertEquals(0, tax.compareTo(BigDecimal.ZERO));
        }
    }

    @Nested
    @DisplayName("Calculate Total Tests")
    class CalculateTotalTests {

        @Test
        @DisplayName("Should calculate total as gross plus commission plus tax")
        void shouldCalculateTotalAsGrossPlusCommissionPlusTax() {
            BigDecimal total = calculator.calculateTotal();

            assertEquals(0, total.compareTo(new BigDecimal("1005.00")));
        }

        @Test
        @DisplayName("Should calculate correct total for small purchase")
        void shouldCalculateCorrectTotalForSmallPurchase() {
            Share smallShare = new Share(stock, new BigDecimal("1"), new BigDecimal("10.00"));
            PurchaseCalculator smallCalculator = new PurchaseCalculator(smallShare);

            BigDecimal total = smallCalculator.calculateTotal();

            assertEquals(0, total.compareTo(new BigDecimal("10.05")));
        }

        @Test
        @DisplayName("Should calculate correct total for large purchase")
        void shouldCalculateCorrectTotalForLargePurchase() {
            Share largeShare = new Share(stock, new BigDecimal("100"), new BigDecimal("500.00"));
            PurchaseCalculator largeCalculator = new PurchaseCalculator(largeShare);

            BigDecimal total = largeCalculator.calculateTotal();

            assertEquals(0, total.compareTo(new BigDecimal("50250.00")));
        }

        @Test
        @DisplayName("Should verify total equals gross plus commission when tax is zero")
        void shouldVerifyTotalEqualsGrossPlusCommissionWhenTaxIsZero() {
            BigDecimal gross = calculator.calculateGross();
            BigDecimal commission = calculator.calculateCommission();
            BigDecimal tax = calculator.calculateTax();
            BigDecimal total = calculator.calculateTotal();

            assertEquals(0, total.compareTo(gross.add(commission).add(tax)));
        }
    }
}
