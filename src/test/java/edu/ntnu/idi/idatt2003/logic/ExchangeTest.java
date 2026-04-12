package edu.ntnu.idi.idatt2003.logic;

import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.transactions.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exchange Tests")
class ExchangeTest {
    private Exchange exchange;
    private Stock appleStock;
    private Stock googleStock;
    private Player player;

    @BeforeEach
    void setUp() {
        appleStock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
        googleStock = new Stock("GOOGL", "Alphabet Inc.", new BigDecimal("2500.00"));
        exchange = new Exchange("NYSE", List.of(appleStock, googleStock));
        player = new Player("John Doe", new BigDecimal("100000.00"));
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create exchange with valid parameters")
        void shouldCreateExchangeWithValidParameters() {
            Exchange newExchange = new Exchange("NASDAQ", List.of(appleStock));

            assertNotNull(newExchange);
            assertEquals("NASDAQ", newExchange.getName());
            assertEquals(1, newExchange.getWeek());
            assertTrue(newExchange.hasStock("AAPL"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when name is null, empty, or whitespace")
        void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
            List<Stock> stocks = List.of(appleStock);

            assertThrows(IllegalArgumentException.class, () ->
                new Exchange(invalidName, stocks)
            );
        }

        @Test
        @DisplayName("Should throw exception when stocks list is null")
        void shouldThrowExceptionWhenStocksListIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new Exchange("NYSE", null)
            );
        }

        @Test
        @DisplayName("Should throw exception when stocks list is empty")
        void shouldThrowExceptionWhenStocksListIsEmpty() {
            List<Stock> emptyStocks = List.of();

            assertThrows(IllegalArgumentException.class, () ->
                new Exchange("NYSE", emptyStocks)
            );
        }

        @Test
        @DisplayName("Should throw exception when stocks list contains null")
        void shouldThrowExceptionWhenStocksListContainsNull() {
            List<Stock> stocks = new ArrayList<>();
            stocks.add(appleStock);
            stocks.add(null);

            assertThrows(IllegalArgumentException.class, () ->
                new Exchange("NYSE", stocks)
            );
        }
    }

    @Nested
    @DisplayName("Stock Lookup Tests")
    class StockLookupTests {

        @Test
        @DisplayName("Should return true for existing stock")
        void shouldReturnTrueForExistingStock() {
            assertTrue(exchange.hasStock("AAPL"));
        }

        @Test
        @DisplayName("Should return false for non-existing stock")
        void shouldReturnFalseForNonExistingStock() {
            assertFalse(exchange.hasStock("MSFT"));
        }

        @Test
        @DisplayName("Should return stock for valid symbol")
        void shouldReturnStockForValidSymbol() {
            Stock stock = exchange.getStock("AAPL");

            assertNotNull(stock);
            assertEquals("AAPL", stock.getSymbol());
        }

        @Test
        @DisplayName("Should return null for non-existing symbol")
        void shouldReturnNullForNonExistingSymbol() {
            assertNull(exchange.getStock("MSFT"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when hasStock symbol is null, empty, or whitespace")
        void shouldThrowExceptionWhenHasStockSymbolIsInvalid(String invalidSymbol) {
            assertThrows(IllegalArgumentException.class, () ->
                exchange.hasStock(invalidSymbol)
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when getStock symbol is null, empty, or whitespace")
        void shouldThrowExceptionWhenGetStockSymbolIsInvalid(String invalidSymbol) {
            assertThrows(IllegalArgumentException.class, () ->
                exchange.getStock(invalidSymbol)
            );
        }
    }

    @Nested
    @DisplayName("Find Stocks Tests")
    class FindStocksTests {

        @Test
        @DisplayName("Should find stock by symbol")
        void shouldFindStockBySymbol() {
            List<Stock> results = exchange.findStocks("AAPL");

            assertEquals(1, results.size());
            assertEquals("AAPL", results.getFirst().getSymbol());
        }

        @Test
        @DisplayName("Should find stock by company name")
        void shouldFindStockByCompanyName() {
            List<Stock> results = exchange.findStocks("Alphabet");

            assertEquals(1, results.size());
            assertEquals("GOOGL", results.getFirst().getSymbol());
        }

        @Test
        @DisplayName("Should find stock by partial match")
        void shouldFindStockByPartialMatch() {
            List<Stock> results = exchange.findStocks("Inc");

            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("Should be case insensitive")
        void shouldBeCaseInsensitive() {
            List<Stock> results = exchange.findStocks("aapl");

            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Should return empty list when no match")
        void shouldReturnEmptyListWhenNoMatch() {
            List<Stock> results = exchange.findStocks("Microsoft");

            assertTrue(results.isEmpty());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when search term is null, empty, or whitespace")
        void shouldThrowExceptionWhenSearchTermIsInvalid(String invalidSearchTerm) {
            assertThrows(IllegalArgumentException.class, () ->
                exchange.findStocks(invalidSearchTerm)
            );
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @DisplayName("Should return gainers ordered by latest price change")
        void shouldReturnGainersOrderedByLatestPriceChange() {
            Stock teslaStock = new Stock("TSLA", "Tesla Inc.", new BigDecimal("100.00"));
            Exchange statisticsExchange = new Exchange("NASDAQ", List.of(appleStock, googleStock, teslaStock));
            appleStock.addNewSalesPrice(new BigDecimal("160.00"));
            googleStock.addNewSalesPrice(new BigDecimal("2505.00"));
            teslaStock.addNewSalesPrice(new BigDecimal("130.00"));

            List<Stock> gainers = statisticsExchange.getGainers(2);

            assertEquals(2, gainers.size());
            assertEquals("TSLA", gainers.get(0).getSymbol());
            assertEquals("AAPL", gainers.get(1).getSymbol());
        }

        @Test
        @DisplayName("Should only return stocks with positive latest price change as gainers")
        void shouldOnlyReturnStocksWithPositiveLatestPriceChangeAsGainers() {
            appleStock.addNewSalesPrice(new BigDecimal("160.00"));
            googleStock.addNewSalesPrice(new BigDecimal("2400.00"));

            List<Stock> gainers = exchange.getGainers(5);

            assertEquals(1, gainers.size());
            assertEquals("AAPL", gainers.getFirst().getSymbol());
        }

        @Test
        @DisplayName("Should return losers ordered by latest price change")
        void shouldReturnLosersOrderedByLatestPriceChange() {
            Stock teslaStock = new Stock("TSLA", "Tesla Inc.", new BigDecimal("100.00"));
            Exchange statisticsExchange = new Exchange("NASDAQ", List.of(appleStock, googleStock, teslaStock));
            appleStock.addNewSalesPrice(new BigDecimal("145.00"));
            googleStock.addNewSalesPrice(new BigDecimal("2450.00"));
            teslaStock.addNewSalesPrice(new BigDecimal("97.00"));

            List<Stock> losers = statisticsExchange.getLosers(2);

            assertEquals(2, losers.size());
            assertEquals("GOOGL", losers.get(0).getSymbol());
            assertEquals("AAPL", losers.get(1).getSymbol());
        }

        @Test
        @DisplayName("Should only return stocks with negative latest price change as losers")
        void shouldOnlyReturnStocksWithNegativeLatestPriceChangeAsLosers() {
            appleStock.addNewSalesPrice(new BigDecimal("145.00"));
            googleStock.addNewSalesPrice(new BigDecimal("2550.00"));

            List<Stock> losers = exchange.getLosers(5);

            assertEquals(1, losers.size());
            assertEquals("AAPL", losers.getFirst().getSymbol());
        }

        @Test
        @DisplayName("Should return fewer stocks than limit when fewer match")
        void shouldReturnFewerStocksThanLimitWhenFewerMatch() {
            appleStock.addNewSalesPrice(new BigDecimal("160.00"));

            List<Stock> gainers = exchange.getGainers(5);

            assertEquals(1, gainers.size());
        }

        @Test
        @DisplayName("Should throw exception when gainers limit is zero")
        void shouldThrowExceptionWhenGainersLimitIsZero() {
            assertThrows(IllegalArgumentException.class, () ->
                exchange.getGainers(0)
            );
        }

        @Test
        @DisplayName("Should throw exception when losers limit is negative")
        void shouldThrowExceptionWhenLosersLimitIsNegative() {
            assertThrows(IllegalArgumentException.class, () ->
                exchange.getLosers(-1)
            );
        }
    }

    @Nested
    @DisplayName("Buy Tests")
    class BuyTests {

        @Test
        @DisplayName("Should complete purchase successfully")
        void shouldCompletePurchaseSuccessfully() {
            Transaction transaction = exchange.buy("AAPL", new BigDecimal("10"), player);

            assertNotNull(transaction);
            assertTrue(transaction.isCommitted());
            assertEquals(1, transaction.getWeek());
            assertEquals(1, player.getPortfolio().getShares().size());
        }

        @Test
        @DisplayName("Should deduct money from player")
        void shouldDeductMoneyFromPlayer() {
            BigDecimal initialMoney = player.getMoney();

            exchange.buy("AAPL", new BigDecimal("10"), player);

            assertTrue(player.getMoney().compareTo(initialMoney) < 0);
        }

        @Test
        @DisplayName("Should add transaction to player archive")
        void shouldAddTransactionToPlayerArchive() {
            exchange.buy("AAPL", new BigDecimal("10"), player);

            assertFalse(player.getTransactionArchive().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception for non-existing stock")
        void shouldThrowExceptionForNonExistingStock() {
            BigDecimal quantity = new BigDecimal("10");

            assertThrows(IllegalArgumentException.class, () ->
                exchange.buy("MSFT", quantity, player)
            );
        }

        @Test
        @DisplayName("Should throw exception when player has insufficient funds")
        void shouldThrowExceptionWhenPlayerHasInsufficientFunds() {
            Player poorPlayer = new Player("Broke Bob", new BigDecimal("1.00"));
            BigDecimal quantity = new BigDecimal("100");

            assertThrows(IllegalStateException.class, () ->
                exchange.buy("GOOGL", quantity, poorPlayer)
            );
        }

        @Test
        @DisplayName("Should throw exception when quantity is null")
        void shouldThrowExceptionWhenQuantityIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                exchange.buy("AAPL", null, player)
            );
        }

        @Test
        @DisplayName("Should throw exception when quantity is zero")
        void shouldThrowExceptionWhenQuantityIsZero() {
            BigDecimal zero = BigDecimal.ZERO;

            assertThrows(IllegalArgumentException.class, () ->
                exchange.buy("AAPL", zero, player)
            );
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            BigDecimal negative = new BigDecimal("-5");

            assertThrows(IllegalArgumentException.class, () ->
                exchange.buy("AAPL", negative, player)
            );
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("Should throw exception when symbol is null, empty, or whitespace")
        void shouldThrowExceptionWhenSymbolIsInvalid(String invalidSymbol) {
            BigDecimal quantity = new BigDecimal("10");

            assertThrows(IllegalArgumentException.class, () ->
                exchange.buy(invalidSymbol, quantity, player)
            );
        }

        @Test
        @DisplayName("Should throw exception when player is null")
        void shouldThrowExceptionWhenPlayerIsNull() {
            BigDecimal quantity = new BigDecimal("10");

            assertThrows(IllegalArgumentException.class, () ->
                exchange.buy("AAPL", quantity, null)
            );
        }
    }

    @Nested
    @DisplayName("Sell Tests")
    class SellTests {

        @Test
        @DisplayName("Should complete sale successfully")
        void shouldCompleteSaleSuccessfully() {
            Transaction buyTransaction = exchange.buy("AAPL", new BigDecimal("10"), player);
            Share share = buyTransaction.getShare();

            Transaction sellTransaction = exchange.sell(share, player);

            assertNotNull(sellTransaction);
            assertTrue(sellTransaction.isCommitted());
            assertEquals(1, sellTransaction.getWeek());
            assertEquals(0, player.getPortfolio().getShares().size());
        }

        @Test
        @DisplayName("Should add money to player")
        void shouldAddMoneyToPlayer() {
            Transaction buyTransaction = exchange.buy("AAPL", new BigDecimal("10"), player);
            Share share = buyTransaction.getShare();
            BigDecimal moneyAfterBuy = player.getMoney();

            exchange.sell(share, player);

            assertTrue(player.getMoney().compareTo(moneyAfterBuy) > 0);
        }

        @Test
        @DisplayName("Should throw exception when player does not own share")
        void shouldThrowExceptionWhenPlayerDoesNotOwnShare() {
            Share unownedShare = new Share(appleStock, new BigDecimal("10"), new BigDecimal("150.00"));

            assertThrows(IllegalStateException.class, () ->
                exchange.sell(unownedShare, player)
            );
        }

        @Test
        @DisplayName("Should throw exception when stock is not listed")
        void shouldThrowExceptionWhenStockIsNotListed() {
            Stock unlistedStock = new Stock("MSFT", "Microsoft Corp", new BigDecimal("300.00"));
            Share unlistedShare = new Share(unlistedStock, new BigDecimal("5"), new BigDecimal("300.00"));
            player.getPortfolio().addShare(unlistedShare);

            assertThrows(IllegalArgumentException.class, () ->
                exchange.sell(unlistedShare, player)
            );
        }

        @Test
        @DisplayName("Should throw exception when share is null")
        void shouldThrowExceptionWhenShareIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                exchange.sell(null, player)
            );
        }

        @Test
        @DisplayName("Should throw exception when player is null")
        void shouldThrowExceptionWhenPlayerIsNull() {
            Share share = new Share(appleStock, new BigDecimal("10"), new BigDecimal("150.00"));

            assertThrows(IllegalArgumentException.class, () ->
                exchange.sell(share, null)
            );
        }
    }

    @Nested
    @DisplayName("Advance Tests")
    class AdvanceTests {

        @Test
        @DisplayName("Should increment week")
        void shouldIncrementWeek() {
            exchange.advance();

            assertEquals(2, exchange.getWeek());
        }

        @Test
        @DisplayName("Should increment week multiple times")
        void shouldIncrementWeekMultipleTimes() {
            exchange.advance();
            exchange.advance();
            exchange.advance();

            assertEquals(4, exchange.getWeek());
        }

        @Test
        @DisplayName("Should keep prices positive after many advances")
        void shouldKeepPricesPositiveAfterManyAdvances() {
            for (int i = 0; i < 100; i++) {
                exchange.advance();
            }

            assertTrue(appleStock.getSalesPrice().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(googleStock.getSalesPrice().compareTo(BigDecimal.ZERO) > 0);
        }
    }
}
