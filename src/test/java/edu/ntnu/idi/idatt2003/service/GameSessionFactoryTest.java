package edu.ntnu.idi.idatt2003.service;

import edu.ntnu.idi.idatt2003.model.Stock;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Game Session Factory Tests")
class GameSessionFactoryTest {

    private final GameSessionFactory factory = new GameSessionFactory();
    private final List<Stock> stocks = List.of(
        new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00")),
        new Stock("MSFT", "Microsoft", new BigDecimal("404.68"))
    );

    @Test
    @DisplayName("Should create game session with valid setup data")
    void shouldCreateGameSessionWithValidSetupData() {
        GameSession session = factory.create("Alice", new BigDecimal("10000.00"), stocks);

        assertNotNull(session);
        assertTrue(session.isActive());
        assertEquals("Alice", session.getPlayer().getName());
        assertEquals(0, new BigDecimal("10000.00").compareTo(session.getPlayer().getMoney()));
        assertEquals("Market", session.getExchange().getName());
        assertTrue(session.getExchange().hasStock("AAPL"));
        assertTrue(session.getExchange().hasStock("MSFT"));
    }

    @Test
    @DisplayName("Should throw exception when player name is invalid")
    void shouldThrowExceptionWhenPlayerNameIsInvalid() {
        BigDecimal startingCapital = new BigDecimal("10000.00");

        assertThrows(IllegalArgumentException.class, () ->
            factory.create(" ", startingCapital, stocks)
        );
    }

    @Test
    @DisplayName("Should throw exception when starting capital is invalid")
    void shouldThrowExceptionWhenStartingCapitalIsInvalid() {
        BigDecimal invalidStartingCapital = new BigDecimal("-1.00");

        assertThrows(IllegalArgumentException.class, () ->
            factory.create("Alice", invalidStartingCapital, stocks)
        );
    }

    @Test
    @DisplayName("Should throw exception when stocks are invalid")
    void shouldThrowExceptionWhenStocksAreInvalid() {
        List<Stock> invalidStocks = List.of();
        BigDecimal startingCapital = new BigDecimal("10000.00");

        assertThrows(IllegalArgumentException.class, () ->
            factory.create("Alice", startingCapital, invalidStocks)
        );
    }
}
