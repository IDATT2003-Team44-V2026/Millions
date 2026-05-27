package edu.ntnu.idi.idatt2003.logic;

import edu.ntnu.idi.idatt2003.model.Difficulty;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.transactions.Transaction;
import edu.ntnu.idi.idatt2003.transactions.TransactionFactory;
import edu.ntnu.idi.idatt2003.transactions.TransactionType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a stock exchange where stocks are listed and can be bought and sold.
 *
 * <p>The exchange maintains a collection of listed stocks, tracks the current week,
 * and provides methods for executing buy and sell transactions on behalf of players. Each week,
 * stock prices can be advanced with random fluctuations to simulate market behaviour.</p>
 */
public class Exchange {

  private final String name;
  private final Map<String, Stock> stockMap;
  private final Random random;
  private final TransactionFactory transactionFactory;
  private final Difficulty difficulty;
  private int week;

  /**
   * Creates a new exchange with the given name, listed stocks, and difficulty.
   *
   * @param name       the name of the exchange; must not be {@code null} or blank
   * @param stocks     the initial list of stocks to be listed on the exchange; must not be
   *                   {@code null}, empty, or contain {@code null} elements
   * @param difficulty the market difficulty controlling drift and volatility; must not be
   *                   {@code null}
   * @throws IllegalArgumentException if {@code name} is {@code null} or blank, if {@code stocks} is
   *                                  {@code null} or empty, if any stock in the list is
   *                                  {@code null}, or if {@code difficulty} is {@code null}
   */
  public Exchange(String name, List<Stock> stocks, Difficulty difficulty) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (stocks == null || stocks.isEmpty()) {
      throw new IllegalArgumentException("Stocks list cannot be null or empty");
    }
    if (difficulty == null) {
      throw new IllegalArgumentException("Difficulty cannot be null");
    }

    this.name = name.trim();
    this.difficulty = difficulty;
    this.week = 1;
    this.stockMap = new HashMap<>();
    this.random = new Random();
    this.transactionFactory = new TransactionFactory();

    for (Stock stock : stocks) {
      if (stock == null) {
        throw new IllegalArgumentException("Stock cannot be null");
      }
      this.stockMap.put(stock.getSymbol(), stock);
    }
  }

  /**
   * Returns the name of this exchange.
   *
   * @return the exchange name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the difficulty level of this exchange.
   *
   * @return the difficulty
   */
  public Difficulty getDifficulty() {
    return difficulty;
  }

  /**
   * Returns the current week number of the exchange.
   *
   * <p>The week starts at 1 when the exchange is created and increments
   * each time {@link #advance()} is called.</p>
   *
   * @return the current week number
   */
  public int getWeek() {
    return week;
  }

  /**
   * Sets the current week number directly. Used when restoring a saved game.
   *
   * @param week the week to restore; must be at least 1
   * @throws IllegalArgumentException if {@code week} is less than 1
   */
  public void setWeek(int week) {
    if (week < 1) {
      throw new IllegalArgumentException("Week cannot be less than 1");
    }
    this.week = week;
  }

  /**
   * Checks whether a stock with the given symbol is listed on this exchange.
   *
   * @param symbol the ticker symbol to look up; must not be {@code null} or blank
   * @return {@code true} if a stock with the given symbol exists on this exchange, {@code false}
   * otherwise
   * @throws IllegalArgumentException if {@code symbol} is {@code null} or blank
   */
  public boolean hasStock(String symbol) {
    if (symbol == null || symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Symbol cannot be null or empty");
    }
    return stockMap.containsKey(symbol);
  }

  /**
   * Returns the stock with the given symbol, or {@code null} if not found.
   *
   * @param symbol the ticker symbol to look up; must not be {@code null} or blank
   * @return the {@link Stock} with the matching symbol, or {@code null} if no stock with that
   * symbol is listed on this exchange
   * @throws IllegalArgumentException if {@code symbol} is {@code null} or blank
   */
  public Stock getStock(String symbol) {
    if (symbol == null || symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Symbol cannot be null or empty");
    }
    return stockMap.get(symbol);
  }

  /**
   * Returns all stocks listed on this exchange, ordered by ticker symbol.
   *
   * @return an unmodifiable list of listed stocks
   */
  public List<Stock> getStocks() {
    return stockMap.values().stream()
        .sorted(Comparator.comparing(Stock::getSymbol))
        .toList();
  }

  /**
   * Searches for stocks whose symbol or company name contains the given search term
   * (case-insensitive).
   *
   * @param searchTerm the term to search for; must not be {@code null} or blank
   * @return an unmodifiable list of stocks matching the search term
   * @throws IllegalArgumentException if {@code searchTerm} is {@code null} or blank
   */
  public List<Stock> findStocks(String searchTerm) {
    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      throw new IllegalArgumentException("Search term cannot be null or empty");
    }
    String searchLower = searchTerm.toLowerCase();
    return stockMap.values().stream()
        .filter(stock -> stock.getSymbol().toLowerCase().contains(searchLower)
            || stock.getCompany().toLowerCase().contains(searchLower))
        .toList();
  }

  /**
   * Returns the stocks with the largest positive price change since the previous week.
   *
   * @param limit the maximum number of stocks to return; must be positive
   * @return a list of gainers ordered from highest to lowest latest price change
   * @throws IllegalArgumentException if {@code limit} is not positive
   */
  public List<Stock> getGainers(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }

    return stockMap.values().stream()
        .filter(stock -> stock.getLatestPriceChangePct().compareTo(BigDecimal.ZERO) > 0)
        .sorted(Comparator.comparing(Stock::getLatestPriceChangePct).reversed())
        .limit(limit)
        .toList();
  }

  /**
   * Returns the stocks with the worst price change since the previous week.
   *
   * @param limit the maximum number of stocks to return; must be positive
   * @return a list of losers ordered from lowest to highest latest price change
   * @throws IllegalArgumentException if {@code limit} is not positive
   */
  public List<Stock> getLosers(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }

    return stockMap.values().stream()
        .filter(stock -> stock.getLatestPriceChangePct().compareTo(BigDecimal.ZERO) < 0)
        .sorted(Comparator.comparing(Stock::getLatestPriceChangePct))
        .limit(limit)
        .toList();
  }

  /**
   * Executes a buy transaction for the specified stock on behalf of a player.
   *
   * <p>A new {@link Share} is created at the stock's current sales price and the
   * resulting transaction is committed against the player's account.</p>
   *
   * @param symbol   the ticker symbol of the stock to buy; must not be {@code null} or blank
   * @param quantity the number of shares to buy; must be positive
   * @param player   the player executing the purchase; must not be {@code null}
   * @return the committed {@link Transaction} representing the purchase
   * @throws IllegalArgumentException if {@code symbol} is {@code null} or blank, if
   *                                  {@code quantity} is {@code null} or not positive, if
   *                                  {@code player} is {@code null}, or if no stock with the given
   *                                  symbol is listed
   */
  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    if (symbol == null || symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Symbol cannot be null or empty");
    }
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    Stock stock = getStock(symbol);
    if (stock == null) {
      throw new IllegalArgumentException(
          "Stock with symbol '" + symbol + "' not found on this exchange"
      );
    }

    Share share = new Share(stock, quantity, stock.getSalesPrice());
    Transaction purchase = transactionFactory.createTransaction(
        TransactionType.PURCHASE,
        share,
        week
    );
    purchase.commit(player);
    return purchase;
  }

  /**
   * Executes a sell transaction for the given share on behalf of a player.
   *
   * <p>The share's stock must be listed on this exchange. A transaction is
   * created and committed against the player's account.</p>
   *
   * @param share  the share to sell; must not be {@code null} and its stock must be listed on this
   *               exchange
   * @param player the player executing the sale; must not be {@code null}
   * @return the committed {@link Transaction} representing the sale
   * @throws IllegalArgumentException if {@code share} is {@code null}, if {@code player} is
   *                                  {@code null}, or if the share's stock is not listed on this
   *                                  exchange
   */
  public Transaction sell(Share share, Player player) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    if (!hasStock(share.stock().getSymbol())) {
      throw new IllegalArgumentException("Stock is not listed on this exchange");
    }

    Transaction sale = transactionFactory.createTransaction(
        TransactionType.SALE,
        share,
        week
    );
    sale.commit(player);
    return sale;
  }

  /**
   * Advances the exchange by one week, updating all stock prices using Geometric Brownian Motion.
   *
   * <p>Prices follow the GBM formula: S(t+dt) = S(t) * exp((μ - σ²/2)*dt + σ*√dt*Z),
   * where Z ~ N(0,1). Drift and volatility are determined by the exchange's
   * {@link Difficulty}.</p>
   */
  public void advance() {
    week++;
    double dt = 1.0 / 52.0;
    double driftTerm = (difficulty.drift - 0.5 * Math.pow(difficulty.volatility, 2)) * dt;
    double volMultiplier = difficulty.volatility * Math.sqrt(dt);

    for (Stock stock : stockMap.values()) {
      double exponent = driftTerm + (volMultiplier * random.nextGaussian());
      BigDecimal newPrice = stock.getSalesPrice()
          .multiply(BigDecimal.valueOf(Math.exp(exponent)))
          .setScale(2, RoundingMode.HALF_UP);
      stock.addNewSalesPrice(newPrice);
    }
  }
}