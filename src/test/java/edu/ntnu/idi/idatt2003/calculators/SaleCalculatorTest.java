package edu.ntnu.idi.idatt2003.calculators;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sale Calculator Tests")
class SaleCalculatorTest {
    private Stock stock;
    private Share profitShare;
    private SaleCalculator profitCalculator;
    private SaleCalculator lossCalculator;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        
        // Profit scenario: bought at 100, selling at 150
        profitShare = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
        profitCalculator = new SaleCalculator(profitShare);
        
        // Loss scenario: bought at 200, selling at 150
        stock.addNewSalesPrice(new BigDecimal("150.00"));
        Share lossShare = new Share(stock, new BigDecimal("10"), new BigDecimal("200.00"));
        lossCalculator = new SaleCalculator(lossShare);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create calculator with valid share")
        void shouldCreateCalculatorWithValidShare() {
            SaleCalculator newCalculator = new SaleCalculator(profitShare);

            assertNotNull(newCalculator);
        }

        @Test
        @DisplayName("Should throw exception when share is null")
        void shouldThrowExceptionWhenShareIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new SaleCalculator(null)
            );
        }
    }

    @Nested
    @DisplayName("Calculate Gross Tests")
    class CalculateGrossTests {

        @Test
        @DisplayName("Should calculate gross as sales price times quantity")
        void shouldCalculateGrossAsSalesPriceTimesQuantity() {
            BigDecimal gross = profitCalculator.calculateGross();

            assertEquals(0, gross.compareTo(new BigDecimal("1500.00")));
        }

        @Test
        @DisplayName("Should calculate gross for fractional quantity")
        void shouldCalculateGrossForFractionalQuantity() {
            Stock testStock = new Stock("GOOGL", "Alphabet", new BigDecimal("100.00"));
            Share fractionalShare = new Share(testStock, new BigDecimal("2.5"), new BigDecimal("80.00"));
            SaleCalculator fractionalCalculator = new SaleCalculator(fractionalShare);

            BigDecimal gross = fractionalCalculator.calculateGross();

            assertEquals(0, gross.compareTo(new BigDecimal("250.00")));
        }

        @Test
        @DisplayName("Should calculate gross for single share")
        void shouldCalculateGrossForSingleShare() {
            Stock testStock = new Stock("MSFT", "Microsoft", new BigDecimal("300.00"));
            Share singleShare = new Share(testStock, new BigDecimal("1"), new BigDecimal("280.00"));
            SaleCalculator singleCalculator = new SaleCalculator(singleShare);

            BigDecimal gross = singleCalculator.calculateGross();

            assertEquals(0, gross.compareTo(new BigDecimal("300.00")));
        }
    }

    @Nested
    @DisplayName("Calculate Commission Tests")
    class CalculateCommissionTests {

        @Test
        @DisplayName("Should calculate commission as 1% of gross")
        void shouldCalculateCommissionAsOnePercentOfGross() {
            BigDecimal commission = profitCalculator.calculateCommission();

            assertEquals(0, commission.compareTo(new BigDecimal("15.00")));
        }

        @Test
        @DisplayName("Should calculate commission correctly for small amounts")
        void shouldCalculateCommissionCorrectlyForSmallAmounts() {
            Stock testStock = new Stock("TEST", "Test Co", new BigDecimal("10.00"));
            Share smallShare = new Share(testStock, new BigDecimal("1"), new BigDecimal("9.00"));
            SaleCalculator smallCalculator = new SaleCalculator(smallShare);

            BigDecimal commission = smallCalculator.calculateCommission();

            assertEquals(0, commission.compareTo(new BigDecimal("0.10")));
        }

        @Test
        @DisplayName("Should calculate commission correctly for large amounts")
        void shouldCalculateCommissionCorrectlyForLargeAmounts() {
            Stock testStock = new Stock("BIG", "Big Corp", new BigDecimal("1000.00"));
            Share largeShare = new Share(testStock, new BigDecimal("1000"), new BigDecimal("900.00"));
            SaleCalculator largeCalculator = new SaleCalculator(largeShare);

            BigDecimal commission = largeCalculator.calculateCommission();

            assertEquals(0, commission.compareTo(new BigDecimal("10000.00")));
        }
    }

    @Nested
    @DisplayName("Calculate Tax Tests - Profit Scenario")
    class CalculateTaxProfitTests {

        @Test
        @DisplayName("Should calculate tax as 30% of profit when profit is positive")
        void shouldCalculateTaxAsThirtyPercentOfProfitWhenProfitIsPositive() {
            BigDecimal tax = profitCalculator.calculateTax();
            
            // Gross: 1500, Commission: 15, Purchase costs: 1000
            // Profit: 1500 - 15 - 1000 = 485
            // Tax: 485 * 0.30 = 145.50

            assertEquals(0, tax.compareTo(new BigDecimal("145.50")));
        }

        @Test
        @DisplayName("Should calculate tax correctly for small profit")
        void shouldCalculateTaxCorrectlyForSmallProfit() {
            Stock testStock = new Stock("TEST", "Test Co", new BigDecimal("11.00"));
            Share smallProfitShare = new Share(testStock, new BigDecimal("10"), new BigDecimal("10.00"));
            SaleCalculator smallProfitCalculator = new SaleCalculator(smallProfitShare);

            BigDecimal tax = smallProfitCalculator.calculateTax();
            
            // Gross: 110, Commission: 1.10, Purchase costs: 100
            // Profit: 110 - 1.10 - 100 = 8.90
            // Tax: 8.90 * 0.30 = 2.67

            assertEquals(0, tax.compareTo(new BigDecimal("2.67")));
        }

        @Test
        @DisplayName("Should calculate tax correctly for large profit")
        void shouldCalculateTaxCorrectlyForLargeProfit() {
            Stock testStock = new Stock("BIG", "Big Corp", new BigDecimal("2000.00"));
            Share largeProfitShare = new Share(testStock, new BigDecimal("100"), new BigDecimal("1000.00"));
            SaleCalculator largeProfitCalculator = new SaleCalculator(largeProfitShare);

            BigDecimal tax = largeProfitCalculator.calculateTax();
            
            // Gross: 200000, Commission: 2000, Purchase costs: 100000
            // Profit: 200000 - 2000 - 100000 = 98000
            // Tax: 98000 * 0.30 = 29400

            assertEquals(0, tax.compareTo(new BigDecimal("29400.00")));
        }
    }

    @Nested
    @DisplayName("Calculate Tax Tests - Loss Scenario")
    class CalculateTaxLossTests {

        @Test
        @DisplayName("Should return zero tax when selling at a loss")
        void shouldReturnZeroTaxWhenSellingAtLoss() {
            BigDecimal tax = lossCalculator.calculateTax();

            assertEquals(0, tax.compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("Should return zero tax when profit is exactly zero")
        void shouldReturnZeroTaxWhenProfitIsExactlyZero() {
            Stock testStock = new Stock("EVEN", "Even Corp", new BigDecimal("101.00"));
            Share evenShare = new Share(testStock, new BigDecimal("100"), new BigDecimal("100.00"));
            SaleCalculator evenCalculator = new SaleCalculator(evenShare);

            BigDecimal tax = evenCalculator.calculateTax();
            
            // Gross: 10100, Commission: 101, Purchase costs: 10000
            // Profit: 10100 - 101 - 10000 = -1 (loss due to commission)

            assertEquals(0, tax.compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("Should return zero tax for large loss")
        void shouldReturnZeroTaxForLargeLoss() {
            Stock testStock = new Stock("CRASH", "Crashed Corp", new BigDecimal("50.00"));
            Share crashShare = new Share(testStock, new BigDecimal("100"), new BigDecimal("200.00"));
            SaleCalculator crashCalculator = new SaleCalculator(crashShare);

            BigDecimal tax = crashCalculator.calculateTax();

            assertEquals(0, tax.compareTo(BigDecimal.ZERO));
        }
    }

    @Nested
    @DisplayName("Calculate Total Tests")
    class CalculateTotalTests {

        @Test
        @DisplayName("Should calculate total as gross minus commission minus tax for profit")
        void shouldCalculateTotalAsGrossMinusCommissionMinusTaxForProfit() {
            BigDecimal total = profitCalculator.calculateTotal();
            
            // Gross: 1500, Commission: 15, Tax: 145.50
            // Total: 1500 - 15 - 145.50 = 1339.50

            assertEquals(0, total.compareTo(new BigDecimal("1339.50")));
        }

        @Test
        @DisplayName("Should calculate total correctly for loss scenario")
        void shouldCalculateTotalCorrectlyForLossScenario() {
            BigDecimal total = lossCalculator.calculateTotal();
            
            // Gross: 1500, Commission: 15, Tax: 0 (loss)
            // Total: 1500 - 15 - 0 = 1485

            assertEquals(0, total.compareTo(new BigDecimal("1485.00")));
        }

        @Test
        @DisplayName("Should calculate correct total for small sale")
        void shouldCalculateCorrectTotalForSmallSale() {
            Stock testStock = new Stock("SMALL", "Small Corp", new BigDecimal("11.00"));
            Share smallShare = new Share(testStock, new BigDecimal("10"), new BigDecimal("10.00"));
            SaleCalculator smallCalculator = new SaleCalculator(smallShare);

            BigDecimal total = smallCalculator.calculateTotal();
            
            // Gross: 110, Commission: 1.10, Tax: 2.67
            // Total: 110 - 1.10 - 2.67 = 106.23

            assertEquals(0, total.compareTo(new BigDecimal("106.23")));
        }

        @Test
        @DisplayName("Should calculate correct total for large sale")
        void shouldCalculateCorrectTotalForLargeSale() {
            Stock testStock = new Stock("MEGA", "Mega Corp", new BigDecimal("1100.00"));
            Share largeShare = new Share(testStock, new BigDecimal("100"), new BigDecimal("1000.00"));
            SaleCalculator largeCalculator = new SaleCalculator(largeShare);

            BigDecimal total = largeCalculator.calculateTotal();
            
            // Gross: 110000, Commission: 1100, Tax: 2670
            // Total: 110000 - 1100 - 2670 = 106230

            assertEquals(0, total.compareTo(new BigDecimal("106230.00")));
        }

        @Test
        @DisplayName("Should verify total equals gross minus commission minus tax")
        void shouldVerifyTotalEqualsGrossMinusCommissionMinusTax() {
            BigDecimal gross = profitCalculator.calculateGross();
            BigDecimal commission = profitCalculator.calculateCommission();
            BigDecimal tax = profitCalculator.calculateTax();
            BigDecimal total = profitCalculator.calculateTotal();

            assertEquals(0, total.compareTo(gross.subtract(commission).subtract(tax)));
        }
    }
}
