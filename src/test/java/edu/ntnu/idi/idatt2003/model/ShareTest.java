package edu.ntnu.idi.idatt2003.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Share Tests")
class ShareTest {
    private Stock stock;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private Share share;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        quantity = new BigDecimal("10");
        purchasePrice = new BigDecimal("145.50");
        share = new Share(stock, quantity, purchasePrice);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create share with valid parameters")
        void shouldCreateShareWithValidParameters() {
            Share newShare = new Share(stock, new BigDecimal("5"), new BigDecimal("100.00"));

            assertNotNull(newShare);
            assertEquals(stock, newShare.getStock());
            assertEquals(new BigDecimal("5"), newShare.getQuantity());
            assertEquals(new BigDecimal("100.00"), newShare.getPurchasePrice());
        }

        @Test
        @DisplayName("Should throw exception when stock is null")
        void shouldThrowExceptionWhenStockIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new Share(null, quantity, purchasePrice)
            );
        }

        @Test
        @DisplayName("Should throw exception when quantity is null")
        void shouldThrowExceptionWhenQuantityIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new Share(stock, null, purchasePrice)
            );
        }

        @Test
        @DisplayName("Should throw exception when quantity is zero")
        void shouldThrowExceptionWhenQuantityIsZero() {
            assertThrows(IllegalArgumentException.class, () ->
                new Share(stock, BigDecimal.ZERO, purchasePrice)
            );
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            BigDecimal negativeQuantity = new BigDecimal("-5");
            assertThrows(IllegalArgumentException.class, () ->
                new Share(stock, negativeQuantity, purchasePrice)
            );
        }

        @Test
        @DisplayName("Should throw exception when purchase price is null")
        void shouldThrowExceptionWhenPurchasePriceIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new Share(stock, quantity, null)
            );
        }

        @Test
        @DisplayName("Should throw exception when purchase price is zero")
        void shouldThrowExceptionWhenPurchasePriceIsZero() {
            assertThrows(IllegalArgumentException.class, () ->
                new Share(stock, quantity, BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("Should throw exception when purchase price is negative")
        void shouldThrowExceptionWhenPurchasePriceIsNegative() {
            BigDecimal negativePrice = new BigDecimal("-100.00");
            assertThrows(IllegalArgumentException.class, () ->
                new Share(stock, quantity, negativePrice)
            );
        }

        @Test
        @DisplayName("Should accept fractional quantities")
        void shouldAcceptFractionalQuantities() {
            BigDecimal fractionalQuantity = new BigDecimal("0.5");
            Share fractionalShare = new Share(stock, fractionalQuantity, purchasePrice);

            assertEquals(fractionalQuantity, fractionalShare.getQuantity());
        }

        @Test
        @DisplayName("Should accept fractional purchase prices")
        void shouldAcceptFractionalPurchasePrices() {
            BigDecimal fractionalPrice = new BigDecimal("99.99");
            Share fractionalShare = new Share(stock, quantity, fractionalPrice);

            assertEquals(fractionalPrice, fractionalShare.getPurchasePrice());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct stock")
        void shouldReturnCorrectStock() {
            assertEquals(stock, share.getStock());
        }

        @Test
        @DisplayName("Should return correct quantity")
        void shouldReturnCorrectQuantity() {
            assertEquals(quantity, share.getQuantity());
        }

        @Test
        @DisplayName("Should return correct purchase price")
        void shouldReturnCorrectPurchasePrice() {
            assertEquals(purchasePrice, share.getPurchasePrice());
        }

        @Test
        @DisplayName("Should return same stock instance")
        void shouldReturnSameStockInstance() {
            Stock retrievedStock = share.getStock();
            assertSame(stock, retrievedStock);
        }
    }
}
