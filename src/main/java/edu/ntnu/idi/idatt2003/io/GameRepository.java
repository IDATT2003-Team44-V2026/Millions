package edu.ntnu.idi.idatt2003.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.idi.idatt2003.logic.Exchange;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.service.GameSession;
import edu.ntnu.idi.idatt2003.transactions.Purchase;
import edu.ntnu.idi.idatt2003.transactions.Sale;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles saving and loading game sessions as JSON files.
 *
 * <p>Save files are stored under {@code $HOME/.millions/saves/} and named after the player
 * (sanitised). Saving overwrites any existing file for that player name.</p>
 */
public class GameRepository {

  private static final Path SAVE_DIR =
      Path.of(System.getProperty("user.home"), ".millions", "saves");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final DateTimeFormatter TIMESTAMP_FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private GameRepository() {
  }

  /**
   * Saves the given game session. Overwrites any existing save for the same player name.
   *
   * @param session the session to save; must not be {@code null}
   * @throws IOException if the file cannot be written
   */
  public static void save(GameSession session) throws IOException {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    Files.createDirectories(SAVE_DIR);
    GameSave dto = toDto(session);
    String filename = sanitizeFilename(session.getPlayer().getName()) + ".json";
    Files.writeString(SAVE_DIR.resolve(filename), GSON.toJson(dto));
  }

  /**
   * Returns all readable save files from the save directory.
   *
   * @return a list of saves; empty if the directory does not exist or is empty
   * @throws IOException if the directory cannot be listed
   */
  public static List<GameSave> listSaves() throws IOException {
    if (!Files.exists(SAVE_DIR)) {
      return List.of();
    }
    List<GameSave> saves = new ArrayList<>();
    try (Stream<Path> paths = Files.list(SAVE_DIR)) {
      for (Path path : paths.filter(p -> p.toString().endsWith(".json")).toList()) {
        try {
          String json = Files.readString(path);
          GameSave save = GSON.fromJson(json, GameSave.class);
          if (save != null && save.playerName != null) {
            saves.add(save);
          }
        } catch (Exception ignored) {
          // skip corrupted files
        }
      }
    }
    return saves;
  }

  /**
   * Reconstructs a {@link GameSession} from the given save.
   *
   * @param save the save to load; must not be {@code null}
   * @return a fully restored game session
   */
  public static GameSession load(GameSave save) {
    if (save == null) {
      throw new IllegalArgumentException("Save cannot be null");
    }

    List<Stock> stocks = rebuildStocks(save.exchange.stocks);
    Exchange exchange = new Exchange(save.exchange.name, stocks);
    exchange.setWeek(save.exchange.week);

    Player player = rebuildPlayer(save.player, stocks);
    return new GameSession(player, exchange);
  }

  /**
   * Deletes the save file associated with the given save.
   *
   * @param save the save to delete; must not be {@code null}
   * @throws IOException if the file cannot be deleted
   */
  public static void delete(GameSave save) throws IOException {
    if (save == null) {
      throw new IllegalArgumentException("Save cannot be null");
    }
    String filename = sanitizeFilename(save.playerName) + ".json";
    Files.deleteIfExists(SAVE_DIR.resolve(filename));
  }

  private static List<Stock> rebuildStocks(List<GameSave.StockData> stockDataList) {
    List<Stock> stocks = new ArrayList<>();
    for (GameSave.StockData sd : stockDataList) {
      List<String> history = sd.priceHistory;
      Stock stock = new Stock(sd.symbol, sd.company, new BigDecimal(history.get(0)));
      for (int i = 1; i < history.size(); i++) {
        stock.addNewSalesPrice(new BigDecimal(history.get(i)));
      }
      stocks.add(stock);
    }
    return stocks;
  }

  private static Player rebuildPlayer(GameSave.PlayerData pd, List<Stock> liveStocks) {
    BigDecimal startingMoney = new BigDecimal(pd.startingMoney);
    BigDecimal currentMoney = new BigDecimal(pd.currentMoney);

    Player player = new Player(pd.name, startingMoney);

    // Adjust balance from startingMoney to actual saved value
    int cmp = currentMoney.compareTo(startingMoney);
    if (cmp > 0) {
      player.addMoney(currentMoney.subtract(startingMoney));
    } else if (cmp < 0) {
      player.withdrawMoney(startingMoney.subtract(currentMoney));
    }

    for (GameSave.ShareData sd : pd.portfolio) {
      Stock stock = findStock(liveStocks, sd.stockSymbol);
      if (stock != null) {
        player.getPortfolio().addShare(
            new Share(stock, new BigDecimal(sd.quantity), new BigDecimal(sd.purchasePrice))
        );
      }
    }

    for (GameSave.TransactionData td : pd.transactions) {
      restoreTransaction(td, liveStocks, player);
    }

    return player;
  }

  private static void restoreTransaction(
      GameSave.TransactionData td,
      List<Stock> liveStocks,
      Player player
  ) {
    BigDecimal quantity = new BigDecimal(td.quantity);
    BigDecimal purchasePrice = new BigDecimal(td.purchasePrice);

    if ("PURCHASE".equals(td.type)) {
      Stock stock = findStock(liveStocks, td.stockSymbol);
      if (stock == null) {
        stock = new Stock(td.stockSymbol, td.stockCompany, purchasePrice);
      }
      Share share = new Share(stock, quantity, purchasePrice);
      player.getTransactionArchive().add(new Purchase(share, td.week));
    } else {
      // For sales, create a snapshot stock so SaleCalculator captures the historical sale price
      BigDecimal salesPrice = new BigDecimal(td.salesPrice);
      Stock snapshotStock = new Stock(td.stockSymbol, td.stockCompany, salesPrice);
      Share share = new Share(snapshotStock, quantity, purchasePrice);
      player.getTransactionArchive().add(new Sale(share, td.week));
    }
  }

  private static Stock findStock(List<Stock> stocks, String symbol) {
    return stocks.stream()
        .filter(s -> s.getSymbol().equals(symbol))
        .findFirst()
        .orElse(null);
  }

  private static GameSave toDto(GameSession session) {
    Player player = session.getPlayer();
    Exchange exchange = session.getExchange();

    GameSave save = new GameSave();
    save.playerName = player.getName();
    save.savedAt = LocalDateTime.now().format(TIMESTAMP_FMT);

    GameSave.PlayerData pd = new GameSave.PlayerData();
    pd.name = player.getName();
    pd.startingMoney = player.getStartingMoney().toPlainString();
    pd.currentMoney = player.getMoney().toPlainString();
    pd.portfolio = player.getPortfolio().getShares().stream()
        .map(share -> {
          GameSave.ShareData sd = new GameSave.ShareData();
          sd.stockSymbol = share.stock().getSymbol();
          sd.quantity = share.quantity().toPlainString();
          sd.purchasePrice = share.purchasePrice().toPlainString();
          return sd;
        }).toList();
    pd.transactions = player.getTransactionArchive().getAllTransactions().stream()
        .map(tx -> {
          GameSave.TransactionData td = new GameSave.TransactionData();
          td.type = (tx instanceof Purchase) ? "PURCHASE" : "SALE";
          td.week = tx.getWeek();
          td.stockSymbol = tx.getShare().stock().getSymbol();
          td.stockCompany = tx.getShare().stock().getCompany();
          td.quantity = tx.getShare().quantity().toPlainString();
          td.purchasePrice = tx.getShare().purchasePrice().toPlainString();
          if (tx instanceof Sale) {
            // Recover historical sale price from gross / quantity
            BigDecimal gross = tx.getCalculator().calculateGross();
            BigDecimal qty = tx.getShare().quantity();
            td.salesPrice = gross.divide(qty, 10, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
          }
          return td;
        }).toList();
    save.player = pd;

    GameSave.ExchangeData ed = new GameSave.ExchangeData();
    ed.name = exchange.getName();
    ed.week = exchange.getWeek();
    ed.stocks = exchange.getStocks().stream()
        .map(stock -> {
          GameSave.StockData sd = new GameSave.StockData();
          sd.symbol = stock.getSymbol();
          sd.company = stock.getCompany();
          sd.priceHistory = stock.getHistoricalPrices().stream()
              .map(BigDecimal::toPlainString)
              .toList();
          return sd;
        }).toList();
    save.exchange = ed;

    return save;
  }

  private static String sanitizeFilename(String name) {
    return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
  }
}
