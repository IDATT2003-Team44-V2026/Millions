package edu.ntnu.idi.idatt2003.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idi.idatt2003.logic.Exchange;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.service.GameSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("GameRepository Tests")
class GameRepositoryTest {

  private static final Path DEFAULT_SAVE_DIR = GameRepository.SAVE_DIR;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    GameRepository.SAVE_DIR = tempDir;
  }

  @AfterEach
  void tearDown() {
    GameRepository.SAVE_DIR = DEFAULT_SAVE_DIR;
  }

  private static GameSession buildSession(String playerName, String capital) {
    Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
    stock.addNewSalesPrice(new BigDecimal("155.00"));
    Exchange exchange = new Exchange("NASDAQ", List.of(stock));
    Player player = new Player(playerName, new BigDecimal(capital));
    return new GameSession(player, exchange);
  }

  @Nested
  @DisplayName("save and load round-trip")
  class RoundTrip {

    @Test
    @DisplayName("Should restore player name and money after round-trip")
    void shouldRestorePlayerNameAndMoney() throws IOException {
      GameRepository.save(buildSession("Alice", "5000.00"));

      List<GameSave> saves = GameRepository.listSaves();
      assertEquals(1, saves.size());
      assertEquals("Alice", saves.get(0).getPlayerName());

      GameSession loaded = GameRepository.load(saves.get(0));
      assertEquals("Alice", loaded.getPlayer().getName());
      assertEquals(0, new BigDecimal("5000.00").compareTo(loaded.getPlayer().getMoney()));
    }

    @Test
    @DisplayName("Should restore exchange week after round-trip")
    void shouldRestoreExchangeWeek() throws IOException {
      GameSession session = buildSession("Bob", "3000.00");
      session.getExchange().setWeek(5);
      GameRepository.save(session);

      GameSession loaded = GameRepository.load(GameRepository.listSaves().get(0));
      assertEquals(5, loaded.getExchange().getWeek());
    }

    @Test
    @DisplayName("Should restore full stock price history after round-trip")
    void shouldRestoreStockPriceHistory() throws IOException {
      GameRepository.save(buildSession("Carol", "2000.00"));

      GameSession loaded = GameRepository.load(GameRepository.listSaves().get(0));
      List<Stock> stocks = loaded.getExchange().getStocks();
      assertEquals(1, stocks.size());
      assertEquals("AAPL", stocks.get(0).getSymbol());
      assertEquals(0, new BigDecimal("155.00").compareTo(stocks.get(0).getSalesPrice()));
    }

    @Test
    @DisplayName("Should restore portfolio share after round-trip")
    void shouldRestorePortfolioShare() throws IOException {
      GameSession session = buildSession("Dave", "10000.00");
      session.buy("AAPL", new BigDecimal("3"));
      GameRepository.save(session);

      GameSession loaded = GameRepository.load(GameRepository.listSaves().get(0));
      assertEquals(1, loaded.getPlayer().getPortfolio().getShares().size());
      Share share = loaded.getPlayer().getPortfolio().getShares().get(0);
      assertEquals("AAPL", share.stock().getSymbol());
      assertEquals(0, new BigDecimal("3").compareTo(share.quantity()));
    }

    @Test
    @DisplayName("Saving twice with same player name overwrites previous save")
    void shouldOverwritePreviousSave() throws IOException {
      GameSession session = buildSession("Eve", "1000.00");
      GameRepository.save(session);
      GameRepository.save(session);

      assertEquals(1, GameRepository.listSaves().size());
    }
  }

  @Nested
  @DisplayName("listSaves")
  class ListSaves {

    @Test
    @DisplayName("Should return empty list when save directory does not exist")
    void shouldReturnEmptyListWhenDirMissing() throws IOException {
      GameRepository.SAVE_DIR = tempDir.resolve("nosuchdir");

      List<GameSave> saves = GameRepository.listSaves();
      assertTrue(saves.isEmpty());
    }

    @Test
    @DisplayName("Should skip and not throw on corrupt JSON file")
    void shouldSkipCorruptFile() throws IOException {
      Files.writeString(tempDir.resolve("bad.json"), "{ not valid json ][");
      GameRepository.save(buildSession("Frank", "500.00"));

      List<GameSave> saves = GameRepository.listSaves();
      assertEquals(1, saves.size());
      assertEquals("Frank", saves.get(0).getPlayerName());
    }

    @Test
    @DisplayName("Should skip structurally incomplete save (missing exchange)")
    void shouldSkipIncompleteSave() throws IOException {
      Files.writeString(tempDir.resolve("incomplete.json"),
          "{\"playerName\":\"Ghost\",\"savedAt\":\"2025-01-01 00:00\"}");
      GameRepository.save(buildSession("Grace", "800.00"));

      List<GameSave> saves = GameRepository.listSaves();
      assertEquals(1, saves.size());
      assertEquals("Grace", saves.get(0).getPlayerName());
    }

    @Test
    @DisplayName("Should sort saves by savedAt descending")
    void shouldSortByTimestampDescending() throws IOException {
      Files.writeString(tempDir.resolve("Alpha.json"), validJson("Alpha", "2025-01-01 09:00"));
      Files.writeString(tempDir.resolve("Beta.json"), validJson("Beta", "2025-01-01 10:00"));

      List<GameSave> saves = GameRepository.listSaves();
      assertEquals("Beta", saves.get(0).getPlayerName());
      assertEquals("Alpha", saves.get(1).getPlayerName());
    }

    private static String validJson(String name, String savedAt) {
      return """
          {
            "version": 1,
            "playerName": "%s",
            "savedAt": "%s",
            "player": {
              "name": "%s",
              "startingMoney": "1000.00",
              "currentMoney": "1000.00",
              "portfolio": [],
              "transactions": []
            },
            "exchange": {
              "name": "NASDAQ",
              "week": 1,
              "stocks": [
                { "symbol": "AAPL", "company": "Apple Inc.", "priceHistory": ["150.00"] }
              ]
            }
          }
          """.formatted(name, savedAt, name);
    }
  }

  @Nested
  @DisplayName("delete")
  class Delete {

    @Test
    @DisplayName("Should remove save file from disk")
    void shouldDeleteSaveFile() throws IOException {
      GameRepository.save(buildSession("Hank", "1000.00"));
      List<GameSave> saves = GameRepository.listSaves();
      assertEquals(1, saves.size());

      GameRepository.delete(saves.get(0));

      assertTrue(GameRepository.listSaves().isEmpty());
    }

    @Test
    @DisplayName("Should not throw when deleting already-missing file")
    void shouldNotThrowWhenFileAlreadyGone() throws IOException {
      GameRepository.save(buildSession("Ivy", "1000.00"));
      GameSave save = GameRepository.listSaves().get(0);
      GameRepository.delete(save);

      assertDoesNotThrow(() -> GameRepository.delete(save));
    }

    @Test
    @DisplayName("Should throw on null save")
    void shouldThrowOnNull() {
      assertThrows(IllegalArgumentException.class, () -> GameRepository.delete(null));
    }
  }
}
