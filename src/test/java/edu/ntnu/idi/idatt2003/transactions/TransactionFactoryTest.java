package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Transaction Factory Tests")
class TransactionFactoryTest {

    private TransactionFactory transactionFactory;
    private Share share;

    @BeforeEach
    void setUp() {
        transactionFactory = new TransactionFactory();

        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("100.00"));
        stock.addNewSalesPrice(new BigDecimal("110.00"));

        share = new Share(
            stock,
            new BigDecimal("1"),
            stock.getSalesPrice()
        );
    }

    @Nested
    @DisplayName("Create Transaction Tests")
    class CreateTransactionTests {

        @Test
        @DisplayName("Should return purchase when type is purchase")
        void shouldReturnPurchaseWhenTypeIsPurchase() {
            Transaction transaction = transactionFactory.createTransaction(
                TransactionType.PURCHASE,
                share,
                1
            );

            assertInstanceOf(Purchase.class, transaction);
        }

        @Test
        @DisplayName("Should return sale when type is sale")
        void shouldReturnSaleWhenTypeIsSale() {
            Transaction transaction = transactionFactory.createTransaction(
                TransactionType.SALE,
                share,
                1
            );

            assertInstanceOf(Sale.class, transaction);
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when transaction type is null")
        void shouldThrowExceptionWhenTransactionTypeIsNull() {
            assertThrows(
                IllegalArgumentException.class,
                () -> transactionFactory.createTransaction(null, share, 1)
            );
        }
    }
}
