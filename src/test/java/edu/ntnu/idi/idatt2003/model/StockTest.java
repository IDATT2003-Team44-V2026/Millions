package edu.ntnu.idi.idatt2003.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stock Tests")
class StockTest {
    private Stock stock;
    private BigDecimal initialPrice;

    @BeforeEach
    void setUp() {
        initialPrice = new BigDecimal("150.50");
        stock = new Stock("AAPL", "Apple Inc.", initialPrice);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create stock with valid parameters")
        void shouldCreateStockWithValidParameters() {
            Stock newStock = new Stock("GOOGL", "Alphabet Inc.", new BigDecimal("2500.00"));

            assertNotNull(newStock);
            assertEquals("GOOGL", newStock.getSymbol());
            assertEquals("Alphabet Inc.", newStock.getCompany());
            assertEquals(new BigDecimal("2500.00"), newStock.getSalesPrice());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when symbol is null, empty, or whitespace")
        void shouldThrowExceptionWhenSymbolIsInvalid(String invalidSymbol) {
            BigDecimal price = new BigDecimal("100.00");
            assertThrows(IllegalArgumentException.class, () ->
                new Stock(invalidSymbol, "Test Company", price)
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when company is null, empty, or whitespace")
        void shouldThrowExceptionWhenCompanyIsInvalid(String invalidCompany) {
            BigDecimal price = new BigDecimal("100.00");
            assertThrows(IllegalArgumentException.class, () ->
                new Stock("TEST", invalidCompany, price)
            );
        }

        @Test
        @DisplayName("Should throw exception when sales price is null")
        void shouldThrowExceptionWhenSalesPriceIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new Stock("TEST", "Test Company", null)
            );
        }

        @Test
        @DisplayName("Should throw exception when sales price is zero")
        void shouldThrowExceptionWhenSalesPriceIsZero() {
            assertThrows(IllegalArgumentException.class, () ->
                new Stock("TEST", "Test Company", BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("Should throw exception when sales price is negative")
        void shouldThrowExceptionWhenSalesPriceIsNegative() {
            BigDecimal negativePrice = new BigDecimal("-10.00");
            assertThrows(IllegalArgumentException.class, () ->
                new Stock("TEST", "Test Company", negativePrice)
            );
        }

        @Test
        @DisplayName("Should trim whitespace from symbol and company")
        void shouldTrimWhitespaceFromSymbolAndCompany() {
            Stock trimmedStock = new Stock("  MSFT  ", "  Microsoft Corp  ", new BigDecimal("300.00"));

            assertEquals("MSFT", trimmedStock.getSymbol());
            assertEquals("Microsoft Corp", trimmedStock.getCompany());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct symbol")
        void shouldReturnCorrectSymbol() {
            assertEquals("AAPL", stock.getSymbol());
        }

        @Test
        @DisplayName("Should return correct company name")
        void shouldReturnCorrectCompanyName() {
            assertEquals("Apple Inc.", stock.getCompany());
        }

        @Test
        @DisplayName("Should return initial sales price")
        void shouldReturnInitialSalesPrice() {
            assertEquals(initialPrice, stock.getSalesPrice());
        }

    }

    @Nested
    @DisplayName("Price Management Tests")
    class PriceManagementTests {

        @Test
        @DisplayName("Should add new sales price successfully")
        void shouldAddNewSalesPriceSuccessfully() {
            BigDecimal newPrice = new BigDecimal("155.75");
            stock.addNewSalesPrice(newPrice);
            assertEquals(newPrice, stock.getSalesPrice());
        }

        @Test
        @DisplayName("Should return most recent price after multiple additions")
        void shouldReturnMostRecentPriceAfterMultipleAdditions() {
            BigDecimal price1 = new BigDecimal("151.00");
            BigDecimal price2 = new BigDecimal("152.50");
            BigDecimal price3 = new BigDecimal("154.25");

            stock.addNewSalesPrice(price1);
            stock.addNewSalesPrice(price2);
            stock.addNewSalesPrice(price3);

            assertEquals(price3, stock.getSalesPrice());
        }

        @Test
        @DisplayName("Should throw exception when adding null price")
        void shouldThrowExceptionWhenAddingNullPrice() {
            assertThrows(IllegalArgumentException.class, () ->
                stock.addNewSalesPrice(null)
            );
        }

        @Test
        @DisplayName("Should throw exception when adding zero price")
        void shouldThrowExceptionWhenAddingZeroPrice() {
            assertThrows(IllegalArgumentException.class, () ->
                stock.addNewSalesPrice(BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("Should throw exception when adding negative price")
        void shouldThrowExceptionWhenAddingNegativePrice() {
            BigDecimal negativePrice = new BigDecimal("-50.00");
            assertThrows(IllegalArgumentException.class, () ->
                stock.addNewSalesPrice(negativePrice)
            );
        }

    }

}
