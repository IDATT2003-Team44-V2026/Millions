package edu.ntnu.idi.idatt2003.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Portfolio Tests")
class PortfolioTest {
    private Portfolio portfolio;
    private Stock appleStock;
    private Stock googleStock;
    private Share appleShare1;
    private Share appleShare2;
    private Share googleShare;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        appleStock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        googleStock = new Stock("GOOGL", "Alphabet Inc.", new BigDecimal("2500.00"));
        appleShare1 = new Share(appleStock, new BigDecimal("10"), new BigDecimal("145.00"));
        appleShare2 = new Share(appleStock, new BigDecimal("5"), new BigDecimal("148.00"));
        googleShare = new Share(googleStock, new BigDecimal("2"), new BigDecimal("2450.00"));
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty portfolio")
        void shouldCreateEmptyPortfolio() {
            Portfolio newPortfolio = new Portfolio();

            assertNotNull(newPortfolio);
            assertTrue(newPortfolio.getShares().isEmpty());
        }
    }

    @Nested
    @DisplayName("Add Share Tests")
    class AddShareTests {

        @Test
        @DisplayName("Should add share to portfolio")
        void shouldAddShareToPortfolio() {
            boolean result = portfolio.addShare(appleShare1);

            assertTrue(result);
            assertEquals(1, portfolio.getShares().size());
            assertTrue(portfolio.contains(appleShare1));
        }

        @Test
        @DisplayName("Should add multiple shares to portfolio")
        void shouldAddMultipleSharesToPortfolio() {
            portfolio.addShare(appleShare1);
            portfolio.addShare(googleShare);

            assertEquals(2, portfolio.getShares().size());
            assertTrue(portfolio.contains(appleShare1));
            assertTrue(portfolio.contains(googleShare));
        }

        @Test
        @DisplayName("Should add multiple shares of same stock")
        void shouldAddMultipleSharesOfSameStock() {
            portfolio.addShare(appleShare1);
            portfolio.addShare(appleShare2);

            assertEquals(2, portfolio.getShares().size());
            assertTrue(portfolio.contains(appleShare1));
            assertTrue(portfolio.contains(appleShare2));
        }

        @Test
        @DisplayName("Should allow adding same share instance twice")
        void shouldAllowAddingSameShareInstanceTwice() {
            portfolio.addShare(appleShare1);
            portfolio.addShare(appleShare1);

            assertEquals(2, portfolio.getShares().size());
            List<Share> shares = portfolio.getShares();
            assertEquals(appleShare1, shares.get(0));
            assertEquals(appleShare1, shares.get(1));
        }

        @Test
        @DisplayName("Should throw exception when adding null share")
        void shouldThrowExceptionWhenAddingNullShare() {
            assertThrows(IllegalArgumentException.class, () ->
                portfolio.addShare(null)
            );
        }
    }

    @Nested
    @DisplayName("Remove Share Tests")
    class RemoveShareTests {

        @Test
        @DisplayName("Should remove share from portfolio")
        void shouldRemoveShareFromPortfolio() {
            portfolio.addShare(appleShare1);

            boolean result = portfolio.removeShare(appleShare1);

            assertTrue(result);
            assertEquals(0, portfolio.getShares().size());
            assertFalse(portfolio.contains(appleShare1));
        }

        @Test
        @DisplayName("Should remove specific share when multiple exist")
        void shouldRemoveSpecificShareWhenMultipleExist() {
            portfolio.addShare(appleShare1);
            portfolio.addShare(googleShare);

            boolean result = portfolio.removeShare(appleShare1);

            assertTrue(result);
            assertEquals(1, portfolio.getShares().size());
            assertFalse(portfolio.contains(appleShare1));
            assertTrue(portfolio.contains(googleShare));
        }

        @Test
        @DisplayName("Should return false when removing non-existent share")
        void shouldReturnFalseWhenRemovingNonExistentShare() {
            portfolio.addShare(googleShare);

            boolean result = portfolio.removeShare(appleShare1);

            assertFalse(result);
            assertEquals(1, portfolio.getShares().size());
        }

        @Test
        @DisplayName("Should return false when removing from empty portfolio")
        void shouldReturnFalseWhenRemovingFromEmptyPortfolio() {
            boolean result = portfolio.removeShare(appleShare1);

            assertFalse(result);
            assertEquals(0, portfolio.getShares().size());
        }

        @Test
        @DisplayName("Should remove only first occurrence when duplicate shares exist")
        void shouldRemoveOnlyFirstOccurrenceWhenDuplicateSharesExist() {
            portfolio.addShare(appleShare1);
            portfolio.addShare(appleShare1);

            boolean result = portfolio.removeShare(appleShare1);

            assertTrue(result);
            assertEquals(1, portfolio.getShares().size());
            assertTrue(portfolio.contains(appleShare1));
        }

        @Test
        @DisplayName("Should throw exception when removing null share")
        void shouldThrowExceptionWhenRemovingNullShare() {
            assertThrows(IllegalArgumentException.class, () ->
                portfolio.removeShare(null)
            );
        }
    }

    @Nested
    @DisplayName("Get All Shares Tests")
    class GetAllSharesTests {

        @Test
        @DisplayName("Should return empty list for empty portfolio")
        void shouldReturnEmptyListForEmptyPortfolio() {
            List<Share> shares = portfolio.getShares();

            assertNotNull(shares);
            assertTrue(shares.isEmpty());
        }

        @Test
        @DisplayName("Should return all shares from portfolio")
        void shouldReturnAllSharesFromPortfolio() {
            portfolio.addShare(appleShare1);
            portfolio.addShare(googleShare);

            List<Share> shares = portfolio.getShares();

            assertEquals(2, shares.size());
            assertTrue(shares.contains(appleShare1));
            assertTrue(shares.contains(googleShare));
        }

        @Test
        @DisplayName("Should return defensive copy of shares list")
        void shouldReturnDefensiveCopyOfSharesList() {
            portfolio.addShare(appleShare1);

            List<Share> shares1 = portfolio.getShares();
            List<Share> shares2 = portfolio.getShares();

            assertNotSame(shares1, shares2);
        }

        @Test
        @DisplayName("Should not affect portfolio when modifying returned list")
        void shouldNotAffectPortfolioWhenModifyingReturnedList() {
            portfolio.addShare(appleShare1);

            List<Share> shares = portfolio.getShares();
            shares.add(googleShare);

            assertEquals(1, portfolio.getShares().size());
            assertFalse(portfolio.contains(googleShare));
        }
    }

    @Nested
    @DisplayName("Get Shares By Symbol Tests")
    class GetSharesBySymbolTests {

        @Test
        @DisplayName("Should return shares matching symbol")
        void shouldReturnSharesMatchingSymbol() {
            portfolio.addShare(appleShare1);
            portfolio.addShare(appleShare2);
            portfolio.addShare(googleShare);

            List<Share> appleShares = portfolio.getShares("AAPL");

            assertEquals(2, appleShares.size());
            assertTrue(appleShares.contains(appleShare1));
            assertTrue(appleShares.contains(appleShare2));
        }

        @Test
        @DisplayName("Should return empty list when no shares match symbol")
        void shouldReturnEmptyListWhenNoSharesMatchSymbol() {
            portfolio.addShare(googleShare);

            List<Share> appleShares = portfolio.getShares("AAPL");

            assertTrue(appleShares.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list for empty portfolio")
        void shouldReturnEmptyListForEmptyPortfolio() {
            List<Share> shares = portfolio.getShares("AAPL");

            assertTrue(shares.isEmpty());
        }

        @Test
        @DisplayName("Should be case insensitive")
        void shouldBeCaseInsensitive() {
            portfolio.addShare(appleShare1);

            List<Share> sharesUpper = portfolio.getShares("AAPL");
            List<Share> sharesLower = portfolio.getShares("aapl");
            List<Share> sharesMixed = portfolio.getShares("AaPl");

            assertEquals(1, sharesUpper.size());
            assertEquals(1, sharesLower.size());
            assertEquals(1, sharesMixed.size());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when symbol is null, empty, or whitespace")
        void shouldThrowExceptionWhenSymbolIsInvalid(String invalidSymbol) {
            assertThrows(IllegalArgumentException.class, () ->
                portfolio.getShares(invalidSymbol)
            );
        }
    }

    @Nested
    @DisplayName("Net Worth Tests")
    class NetWorthTests {

        @Test
        @DisplayName("Should return zero net worth for empty portfolio")
        void shouldReturnZeroNetWorthForEmptyPortfolio() {
            assertEquals(0, BigDecimal.ZERO.compareTo(portfolio.getNetWorth()));
        }

        @Test
        @DisplayName("Should return net worth for single share")
        void shouldReturnNetWorthForSingleShare() {
            portfolio.addShare(appleShare1);

            assertEquals(0, new BigDecimal("1474.5000").compareTo(portfolio.getNetWorth()));
        }

        @Test
        @DisplayName("Should return total net worth for multiple shares")
        void shouldReturnTotalNetWorthForMultipleShares() {
            portfolio.addShare(appleShare1);
            portfolio.addShare(googleShare);

            assertEquals(0, new BigDecimal("6409.5000").compareTo(portfolio.getNetWorth()));
        }
    }

    @Nested
    @DisplayName("Contains Tests")
    class ContainsTests {

        @Test
        @DisplayName("Should return true when share exists in portfolio")
        void shouldReturnTrueWhenShareExistsInPortfolio() {
            portfolio.addShare(appleShare1);

            assertTrue(portfolio.contains(appleShare1));
        }

        @Test
        @DisplayName("Should return false when share does not exist in portfolio")
        void shouldReturnFalseWhenShareDoesNotExistInPortfolio() {
            portfolio.addShare(googleShare);

            assertFalse(portfolio.contains(appleShare1));
        }

        @Test
        @DisplayName("Should return false for empty portfolio")
        void shouldReturnFalseForEmptyPortfolio() {
            assertFalse(portfolio.contains(appleShare1));
        }

        @Test
        @DisplayName("Should distinguish between different share instances")
        void shouldDistinguishBetweenDifferentShareInstances() {
            portfolio.addShare(appleShare1);

            assertTrue(portfolio.contains(appleShare1));
            assertFalse(portfolio.contains(appleShare2));
        }

        @Test
        @DisplayName("Should throw exception when checking for null share")
        void shouldThrowExceptionWhenCheckingForNullShare() {
            assertThrows(IllegalArgumentException.class, () ->
                portfolio.contains(null)
            );
        }
    }
}
