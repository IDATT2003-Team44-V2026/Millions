package edu.ntnu.idi.idatt2003.calculators;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

// assertEquals with bigDecimal checks trailing zeroes, use compareTo instead

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

        @ParameterizedTest(name = "Quantity: {0}, Purchase Price: {1} -> Gross: {2}")
        @CsvSource({
            "10, 100.00, 1000.00",
            "2.5, 100.00, 250.00",
            "10, 99.99, 999.90",
            "1, 100.00, 100.00"
        })
        @DisplayName("Should calculate gross correctly for various quantity and price combinations")
        void shouldCalculateGrossCorrectly(String quantityStr, String priceStr, String expectedGrossStr) {
            BigDecimal qty = new BigDecimal(quantityStr);
            BigDecimal price = new BigDecimal(priceStr);
            BigDecimal expectedGross = new BigDecimal(expectedGrossStr);
            
            Share customShare = new Share(stock, qty, price);
            PurchaseCalculator customCalculator = new PurchaseCalculator(customShare);

            BigDecimal gross = customCalculator.calculateGross();

            assertEquals(0, gross.compareTo(expectedGross));
        }
    }

    @Nested
    @DisplayName("Calculate Commission Tests")
    class CalculateCommissionTests {

        @ParameterizedTest(name = "Quantity: {0}, Purchase Price: {1} -> Commission: {2}")
        @CsvSource({
            "10, 100.00, 5.00",
            "1, 10.00, 0.05",
            "1000, 1000.00, 5000.00"
        })
        @DisplayName("Should calculate commission as 0.5% of gross")
        void shouldCalculateCommissionCorrectly(String quantityStr, String priceStr, String expectedCommissionStr) {
            BigDecimal qty = new BigDecimal(quantityStr);
            BigDecimal price = new BigDecimal(priceStr);
            BigDecimal expectedCommission = new BigDecimal(expectedCommissionStr);
            
            Share customShare = new Share(stock, qty, price);
            PurchaseCalculator customCalculator = new PurchaseCalculator(customShare);

            BigDecimal commission = customCalculator.calculateCommission();

            assertEquals(0, commission.compareTo(expectedCommission));
        }
    }

    @Nested
    @DisplayName("Calculate Tax Tests")
    class CalculateTaxTests {

        @ParameterizedTest(name = "Quantity: {0}, Price: {1} -> Tax: 0")
        @CsvSource({
            "10, 100.00",
            "1000, 5000.00"
        })
        @DisplayName("Should return zero tax for purchase regardless of amount")
        void shouldReturnZeroTaxForPurchase(String quantityStr, String priceStr) {
            Share customShare = new Share(stock, new BigDecimal(quantityStr), new BigDecimal(priceStr));
            PurchaseCalculator customCalculator = new PurchaseCalculator(customShare);

            BigDecimal tax = customCalculator.calculateTax();

            assertEquals(0, tax.compareTo(BigDecimal.ZERO));
        }
    }

    @Nested
    @DisplayName("Calculate Total Tests")
    class CalculateTotalTests {

        @ParameterizedTest(name = "Quantity: {0}, Price: {1} -> Total: {2}")
        @CsvSource({
            "10, 100.00, 1005.00",
            "1, 10.00, 10.05",
            "100, 500.00, 50250.00"
        })
        @DisplayName("Should calculate total as gross plus commission plus tax")
        void shouldCalculateTotalCorrectly(String quantityStr, String priceStr, String expectedTotalStr) {
            BigDecimal qty = new BigDecimal(quantityStr);
            BigDecimal price = new BigDecimal(priceStr);
            BigDecimal expectedTotal = new BigDecimal(expectedTotalStr);
            
            Share customShare = new Share(stock, qty, price);
            PurchaseCalculator customCalculator = new PurchaseCalculator(customShare);

            BigDecimal total = customCalculator.calculateTotal();

            assertEquals(0, total.compareTo(expectedTotal));
        }

        @Test
        @DisplayName("Should verify total equals sum of components")
        void shouldVerifyTotalEqualsSumOfComponents() {
            BigDecimal gross = calculator.calculateGross();
            BigDecimal commission = calculator.calculateCommission();
            BigDecimal tax = calculator.calculateTax();
            BigDecimal total = calculator.calculateTotal();

            assertEquals(0, total.compareTo(gross.add(commission).add(tax)));
        }
    }
}
