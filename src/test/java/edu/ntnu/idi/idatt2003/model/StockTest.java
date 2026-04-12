package edu.ntnu.idi.idatt2003.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

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
        @DisplayName("Should throw exception when company is invalid")
        void shouldThrowExceptionWhenCompanyIsInvalid(String invalidCompany) {
            BigDecimal price = new BigDecimal("100.00");
            assertThrows(IllegalArgumentException.class, () ->
                new Stock("TEST", invalidCompany, price)
            );
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"0.0", "-10.00"})
        @DisplayName("Should throw exception when sales price is invalid (null, zero, or negative)")
        void shouldThrowExceptionWhenSalesPriceIsInvalid(String invalidPrice) {
            BigDecimal price = invalidPrice == null ? null : new BigDecimal(invalidPrice);
            assertThrows(IllegalArgumentException.class, () ->
                new Stock("TEST", "Test Company", price)
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
    @DisplayName("Price Management Tests")
    class PriceManagementTests {

        @Test
        @DisplayName("Should addNewSalesPrice successfully")
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

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"0.0", "-50.00"})
        @DisplayName("Should throw exception when adding invalid price (null, zero, or negative)")
        void shouldThrowExceptionWhenAddingInvalidPrice(String invalidPrice) {
            BigDecimal price = invalidPrice == null ? null : new BigDecimal(invalidPrice);
            assertThrows(IllegalArgumentException.class, () ->
                stock.addNewSalesPrice(price)
            );
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @DisplayName("Should return complete historical prices")
        void shouldReturnCompleteHistoricalPrices() {
            BigDecimal price1 = new BigDecimal("151.00");
            BigDecimal price2 = new BigDecimal("152.50");
            stock.addNewSalesPrice(price1);
            stock.addNewSalesPrice(price2);

            List<BigDecimal> historicalPrices = stock.getHistoricalPrices();

            assertEquals(3, historicalPrices.size());
            assertEquals(initialPrice, historicalPrices.get(0));
            assertEquals(price1, historicalPrices.get(1));
            assertEquals(price2, historicalPrices.get(2));
        }

        @Test
        @DisplayName("Should return defensive copy of historical prices")
        void shouldReturnDefensiveCopyOfHistoricalPrices() {
            List<BigDecimal> historicalPrices = stock.getHistoricalPrices();

            historicalPrices.add(new BigDecimal("999.99"));

            assertEquals(1, stock.getHistoricalPrices().size());
        }

        @Test
        @DisplayName("Should return highest registered price")
        void shouldReturnHighestRegisteredPrice() {
            stock.addNewSalesPrice(new BigDecimal("160.00"));
            stock.addNewSalesPrice(new BigDecimal("149.75"));
            stock.addNewSalesPrice(new BigDecimal("155.50"));

            assertEquals(0, new BigDecimal("160.00").compareTo(stock.getHighestPrice()));
        }

        @Test
        @DisplayName("Should return lowest registered price")
        void shouldReturnLowestRegisteredPrice() {
            stock.addNewSalesPrice(new BigDecimal("160.00"));
            stock.addNewSalesPrice(new BigDecimal("149.75"));
            stock.addNewSalesPrice(new BigDecimal("155.50"));

            assertEquals(0, new BigDecimal("149.75").compareTo(stock.getLowestPrice()));
        }

        @Test
        @DisplayName("Should return zero latest price change when only one price exists")
        void shouldReturnZeroLatestPriceChangeWhenOnlyOnePriceExists() {
            assertEquals(0, BigDecimal.ZERO.compareTo(stock.getLatestPriceChange()));
        }

        @Test
        @DisplayName("Should return positive latest price change")
        void shouldReturnPositiveLatestPriceChange() {
            stock.addNewSalesPrice(new BigDecimal("160.00"));

            assertEquals(0, new BigDecimal("9.50").compareTo(stock.getLatestPriceChange()));
        }

        @Test
        @DisplayName("Should return negative latest price change")
        void shouldReturnNegativeLatestPriceChange() {
            stock.addNewSalesPrice(new BigDecimal("140.00"));

            assertEquals(0, new BigDecimal("-10.50").compareTo(stock.getLatestPriceChange()));
        }
    }
}
