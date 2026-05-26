package edu.ntnu.idi.idatt2003.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt2003.logic.Exchange;
import edu.ntnu.idi.idatt2003.model.Difficulty;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Stock;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Game Session Tests")
class GameSessionTest {

    private Stock appleStock;
    private Player player;
    private GameSession session;
    private AtomicInteger notifications;

    @BeforeEach
    void setUp() {
        appleStock = new Stock("AAPL", "Apple Inc.", new BigDecimal("100.00"));
        player = new Player("Alice", new BigDecimal("1000.00"));
        session = new GameSession(player, new Exchange("NASDAQ", List.of(appleStock), Difficulty.NORMAL));
        notifications = new AtomicInteger();
        session.addObserver(notifications::incrementAndGet);
    }

    @Test
    @DisplayName("Should buy stock and notify observers")
    void shouldBuyStockAndNotifyObservers() {
        session.buy("AAPL", new BigDecimal("2.5"));

        assertEquals(1, player.getPortfolio().getShares().size());
        assertEquals(1, player.getTransactionArchive().getPurchases(1).size());
        assertTrue(player.getMoney().compareTo(new BigDecimal("1000.00")) < 0);
        assertEquals(1, notifications.get());
    }

    @Test
    @DisplayName("Should not notify observers when buy fails")
    void shouldNotNotifyObserversWhenBuyFails() {
        BigDecimal quantity = new BigDecimal("100");

        assertThrows(IllegalStateException.class, () ->
            session.buy("AAPL", quantity)
        );

        assertTrue(player.getPortfolio().getShares().isEmpty());
        assertTrue(player.getTransactionArchive().isEmpty());
        assertEquals(new BigDecimal("1000.00"), player.getMoney());
        assertEquals(0, notifications.get());
    }

    @Test
    @DisplayName("Should sell share and notify observers")
    void shouldSellShareAndNotifyObservers() {
        session.buy("AAPL", new BigDecimal("2"));
        var share = player.getPortfolio().getShares().getFirst();
        BigDecimal moneyAfterBuy = player.getMoney();

        session.sell(share);

        assertTrue(player.getPortfolio().getShares().isEmpty());
        assertEquals(1, player.getTransactionArchive().getSales(1).size());
        assertTrue(player.getMoney().compareTo(moneyAfterBuy) > 0);
        assertEquals(2, notifications.get());
    }

    @Test
    @DisplayName("Should advance week and notify observers")
    void shouldAdvanceWeekAndNotifyObservers() {
        int weekBefore = session.getExchange().getWeek();

        session.advanceWeek();

        assertEquals(weekBefore + 1, session.getExchange().getWeek());
        assertEquals(1, notifications.get());
    }

    @Test
    @DisplayName("Should end session and notify observers")
    void shouldEndSessionAndNotifyObservers() {
        assertTrue(session.isActive());

        session.endSession();

        assertFalse(session.isActive());
        assertEquals(1, notifications.get());
    }

    @Test
    @DisplayName("Should not notify observers when sell fails")
    void shouldNotNotifyObserversWhenSellFails() {
        var share = new edu.ntnu.idi.idatt2003.model.Share(
            appleStock,
            new BigDecimal("1"),
            new BigDecimal("100.00")
        );

        assertThrows(IllegalStateException.class, () -> session.sell(share));

        assertTrue(player.getPortfolio().getShares().isEmpty());
        assertTrue(player.getTransactionArchive().isEmpty());
        assertEquals(new BigDecimal("1000.00"), player.getMoney());
        assertEquals(0, notifications.get());
    }

    @Nested
    @DisplayName("Sell All Tests")
    class SellAllTests {

        @Test
        @DisplayName("Should empty portfolio after selling all")
        void shouldEmptyPortfolioAfterSellingAll() {
            session.buy("AAPL", new BigDecimal("3"));

            session.sellAll();

            assertTrue(player.getPortfolio().getShares().isEmpty());
        }

        @Test
        @DisplayName("Should increase balance after selling all")
        void shouldIncreaseBalanceAfterSellingAll() {
            session.buy("AAPL", new BigDecimal("3"));
            BigDecimal balanceAfterBuy = player.getMoney();

            session.sellAll();

            assertTrue(player.getMoney().compareTo(balanceAfterBuy) > 0);
        }

        @Test
        @DisplayName("Should notify observers exactly once after selling all")
        void shouldNotifyObserversOnceAfterSellingAll() {
            session.buy("AAPL", new BigDecimal("2"));
            notifications.set(0);

            session.sellAll();

            assertEquals(1, notifications.get());
        }

        @Test
        @DisplayName("Should still notify observers when portfolio is empty")
        void shouldNotifyObserversWhenPortfolioIsEmpty() {
            session.sellAll();

            assertEquals(1, notifications.get());
        }
    }
}
