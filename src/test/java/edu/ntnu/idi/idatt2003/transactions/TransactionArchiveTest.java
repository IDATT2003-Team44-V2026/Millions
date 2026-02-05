package edu.ntnu.idi.idatt2003.transactions;

import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TransactionArchive Tests")
class TransactionArchiveTest {
    private TransactionArchive archive;
    private Stock stock;
    private Share share1;
    private Share share2;

    @BeforeEach
    void setUp() {
        archive = new TransactionArchive();
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        share1 = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
        share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("120.00"));
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty archive")
        void shouldCreateEmptyArchive() {
            TransactionArchive newArchive = new TransactionArchive();

            assertTrue(newArchive.isEmpty());
        }
    }

    @Nested
    @DisplayName("Add Transaction Tests")
    class AddTransactionTests {

        @Test
        @DisplayName("Should add purchase successfully")
        void shouldAddPurchaseSuccessfully() {
            Purchase purchase = new Purchase(share1, 1);

            assertTrue(archive.add(purchase));
            assertFalse(archive.isEmpty());
        }

        @Test
        @DisplayName("Should add sale successfully")
        void shouldAddSaleSuccessfully() {
            Sale sale = new Sale(share1, 1);

            assertTrue(archive.add(sale));
            assertFalse(archive.isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when adding null transaction")
        void shouldThrowExceptionWhenAddingNullTransaction() {
            assertThrows(IllegalArgumentException.class, () ->
                archive.add(null)
            );
        }

        @Test
        @DisplayName("Should add multiple transactions")
        void shouldAddMultipleTransactions() {
            Purchase purchase = new Purchase(share1, 1);
            Sale sale = new Sale(share2, 2);

            archive.add(purchase);
            archive.add(sale);

            assertFalse(archive.isEmpty());
            assertEquals(2, archive.getTransactions(1).size() + archive.getTransactions(2).size());
        }
    }

    @Nested
    @DisplayName("isEmpty Tests")
    class IsEmptyTests {

        @Test
        @DisplayName("Should return true when archive is empty")
        void shouldReturnTrueWhenArchiveIsEmpty() {
            assertTrue(archive.isEmpty());
        }

        @Test
        @DisplayName("Should return false when archive has transactions")
        void shouldReturnFalseWhenArchiveHasTransactions() {
            Purchase purchase = new Purchase(share1, 1);
            archive.add(purchase);

            assertFalse(archive.isEmpty());
        }
    }

    @Nested
    @DisplayName("getTransactions Tests")
    class GetTransactionsTests {

        @Test
        @DisplayName("Should return empty list when no transactions")
        void shouldReturnEmptyListWhenNoTransactions() {
            List<Transaction> transactions = archive.getTransactions(1);

            assertNotNull(transactions);
            assertTrue(transactions.isEmpty());
        }

        @Test
        @DisplayName("Should return transactions for specific week")
        void shouldReturnTransactionsForSpecificWeek() {
            Purchase purchase = new Purchase(share1, 1);
            Sale sale = new Sale(share2, 1);
            archive.add(purchase);
            archive.add(sale);

            List<Transaction> transactions = archive.getTransactions(1);

            assertEquals(2, transactions.size());
            assertTrue(transactions.contains(purchase));
            assertTrue(transactions.contains(sale));
        }

        @Test
        @DisplayName("Should return empty list for week with no transactions")
        void shouldReturnEmptyListForWeekWithNoTransactions() {
            Purchase purchase = new Purchase(share1, 1);
            archive.add(purchase);

            List<Transaction> transactions = archive.getTransactions(2);

            assertTrue(transactions.isEmpty());
        }

        @Test
        @DisplayName("Should filter correctly by week")
        void shouldFilterCorrectlyByWeek() {
            Purchase purchase1 = new Purchase(share1, 1);
            Purchase purchase2 = new Purchase(share2, 2);
            Sale sale1 = new Sale(share1, 1);
            archive.add(purchase1);
            archive.add(purchase2);
            archive.add(sale1);

            List<Transaction> week1Transactions = archive.getTransactions(1);
            List<Transaction> week2Transactions = archive.getTransactions(2);

            assertEquals(2, week1Transactions.size());
            assertEquals(1, week2Transactions.size());
            assertTrue(week1Transactions.contains(purchase1));
            assertTrue(week1Transactions.contains(sale1));
            assertTrue(week2Transactions.contains(purchase2));
        }

        @Test
        @DisplayName("Should throw exception when week is negative")
        void shouldThrowExceptionWhenWeekIsNegative() {
            assertThrows(IllegalArgumentException.class, () ->
                archive.getTransactions(-1)
            );
        }

        @Test
        @DisplayName("Should allow week zero")
        void shouldAllowWeekZero() {
            Purchase purchase = new Purchase(share1, 0);
            archive.add(purchase);

            List<Transaction> transactions = archive.getTransactions(0);

            assertEquals(1, transactions.size());
        }
    }

    @Nested
    @DisplayName("getPurchases Tests")
    class GetPurchasesTests {

        @Test
        @DisplayName("Should return only purchases for specific week")
        void shouldReturnOnlyPurchasesForSpecificWeek() {
            Purchase purchase1 = new Purchase(share1, 1);
            Purchase purchase2 = new Purchase(share2, 1);
            Sale sale = new Sale(share1, 1);
            archive.add(purchase1);
            archive.add(purchase2);
            archive.add(sale);

            List<Purchase> purchases = archive.getPurchases(1);

            assertEquals(2, purchases.size());
            assertTrue(purchases.contains(purchase1));
            assertTrue(purchases.contains(purchase2));
        }

        @Test
        @DisplayName("Should exclude sales from purchases")
        void shouldExcludeSalesFromPurchases() {
            Purchase purchase = new Purchase(share1, 1);
            Sale sale = new Sale(share2, 1);
            archive.add(purchase);
            archive.add(sale);

            List<Purchase> purchases = archive.getPurchases(1);

            assertEquals(1, purchases.size());
            assertFalse(purchases.contains(sale));
        }

        @Test
        @DisplayName("Should return empty list when no purchases in week")
        void shouldReturnEmptyListWhenNoPurchasesInWeek() {
            Sale sale = new Sale(share1, 1);
            archive.add(sale);

            List<Purchase> purchases = archive.getPurchases(1);

            assertTrue(purchases.isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when week is negative")
        void shouldThrowExceptionWhenWeekIsNegative() {
            assertThrows(IllegalArgumentException.class, () ->
                archive.getPurchases(-1)
            );
        }

        @Test
        @DisplayName("Should filter purchases by week correctly")
        void shouldFilterPurchasesByWeekCorrectly() {
            Purchase purchase1 = new Purchase(share1, 1);
            Purchase purchase2 = new Purchase(share2, 2);
            archive.add(purchase1);
            archive.add(purchase2);

            List<Purchase> week1Purchases = archive.getPurchases(1);
            List<Purchase> week2Purchases = archive.getPurchases(2);

            assertEquals(1, week1Purchases.size());
            assertEquals(1, week2Purchases.size());
            assertTrue(week1Purchases.contains(purchase1));
            assertTrue(week2Purchases.contains(purchase2));
        }
    }

    @Nested
    @DisplayName("getSales Tests")
    class GetSalesTests {

        @Test
        @DisplayName("Should return only sales for specific week")
        void shouldReturnOnlySalesForSpecificWeek() {
            Sale sale1 = new Sale(share1, 1);
            Sale sale2 = new Sale(share2, 1);
            Purchase purchase = new Purchase(share1, 1);
            archive.add(sale1);
            archive.add(sale2);
            archive.add(purchase);

            List<Sale> sales = archive.getSales(1);

            assertEquals(2, sales.size());
            assertTrue(sales.contains(sale1));
            assertTrue(sales.contains(sale2));
        }

        @Test
        @DisplayName("Should exclude purchases from sales")
        void shouldExcludePurchasesFromSales() {
            Purchase purchase = new Purchase(share1, 1);
            Sale sale = new Sale(share2, 1);
            archive.add(purchase);
            archive.add(sale);

            List<Sale> sales = archive.getSales(1);

            assertEquals(1, sales.size());
            assertFalse(sales.contains(purchase));
        }

        @Test
        @DisplayName("Should return empty list when no sales in week")
        void shouldReturnEmptyListWhenNoSalesInWeek() {
            Purchase purchase = new Purchase(share1, 1);
            archive.add(purchase);

            List<Sale> sales = archive.getSales(1);

            assertTrue(sales.isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when week is negative")
        void shouldThrowExceptionWhenWeekIsNegative() {
            assertThrows(IllegalArgumentException.class, () ->
                archive.getSales(-1)
            );
        }

        @Test
        @DisplayName("Should filter sales by week correctly")
        void shouldFilterSalesByWeekCorrectly() {
            Sale sale1 = new Sale(share1, 1);
            Sale sale2 = new Sale(share2, 2);
            archive.add(sale1);
            archive.add(sale2);

            List<Sale> week1Sales = archive.getSales(1);
            List<Sale> week2Sales = archive.getSales(2);

            assertEquals(1, week1Sales.size());
            assertEquals(1, week2Sales.size());
            assertTrue(week1Sales.contains(sale1));
            assertTrue(week2Sales.contains(sale2));
        }
    }

    @Nested
    @DisplayName("countDistinctWeeks Tests")
    class CountDistinctWeeksTests {

        @Test
        @DisplayName("Should return zero when archive is empty")
        void shouldReturnZeroWhenArchiveIsEmpty() {
            assertEquals(0, archive.countDistinctWeeks());
        }

        @Test
        @DisplayName("Should count single week")
        void shouldCountSingleWeek() {
            Purchase purchase = new Purchase(share1, 1);
            archive.add(purchase);

            assertEquals(1, archive.countDistinctWeeks());
        }

        @Test
        @DisplayName("Should count distinct weeks correctly")
        void shouldCountDistinctWeeksCorrectly() {
            Purchase purchase1 = new Purchase(share1, 1);
            Purchase purchase2 = new Purchase(share2, 2);
            Sale sale = new Sale(share1, 3);
            archive.add(purchase1);
            archive.add(purchase2);
            archive.add(sale);

            assertEquals(3, archive.countDistinctWeeks());
        }

        @Test
        @DisplayName("Should not count duplicate weeks")
        void shouldNotCountDuplicateWeeks() {
            Purchase purchase1 = new Purchase(share1, 1);
            Purchase purchase2 = new Purchase(share2, 1);
            Sale sale = new Sale(share1, 1);
            archive.add(purchase1);
            archive.add(purchase2);
            archive.add(sale);

            assertEquals(1, archive.countDistinctWeeks());
        }

        @Test
        @DisplayName("Should count correctly with mixed weeks")
        void shouldCountCorrectlyWithMixedWeeks() {
            Purchase purchase1 = new Purchase(share1, 1);
            Purchase purchase2 = new Purchase(share2, 1);
            Sale sale1 = new Sale(share1, 2);
            Sale sale2 = new Sale(share2, 2);
            Purchase purchase3 = new Purchase(share1, 5);
            archive.add(purchase1);
            archive.add(purchase2);
            archive.add(sale1);
            archive.add(sale2);
            archive.add(purchase3);

            assertEquals(3, archive.countDistinctWeeks());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complex transaction history")
        void shouldHandleComplexTransactionHistory() {
            Purchase p1 = new Purchase(share1, 1);
            Purchase p2 = new Purchase(share2, 1);
            Sale s1 = new Sale(share1, 2);
            Purchase p3 = new Purchase(share1, 3);
            Sale s2 = new Sale(share2, 3);
            Sale s3 = new Sale(share1, 3);

            archive.add(p1);
            archive.add(p2);
            archive.add(s1);
            archive.add(p3);
            archive.add(s2);
            archive.add(s3);

            assertEquals(6, archive.getTransactions(1).size() + 
                         archive.getTransactions(2).size() + 
                         archive.getTransactions(3).size());
            assertEquals(3, archive.countDistinctWeeks());
            assertEquals(2, archive.getPurchases(1).size());
            assertEquals(1, archive.getSales(2).size());
            assertEquals(2, archive.getSales(3).size());
        }

        @Test
        @DisplayName("Should maintain transaction order")
        void shouldMaintainTransactionOrder() {
            Purchase p1 = new Purchase(share1, 1);
            Sale s1 = new Sale(share2, 1);
            Purchase p2 = new Purchase(share1, 1);

            archive.add(p1);
            archive.add(s1);
            archive.add(p2);

            List<Transaction> transactions = archive.getTransactions(1);

            assertEquals(3, transactions.size());
            assertSame(p1, transactions.get(0));
            assertSame(s1, transactions.get(1));
            assertSame(p2, transactions.get(2));
        }
    }
}
