package edu.ntnu.idi.idatt2003.logic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.model.Stock;
import edu.ntnu.idi.idatt2003.transactions.Purchase;
import edu.ntnu.idi.idatt2003.transactions.Sale;
import edu.ntnu.idi.idatt2003.transactions.Transaction;

/**
 * Represents a stock exchange where stocks are listed and can be bought and sold.
 *
 * <p>The exchange maintains a collection of listed stocks, tracks the current week,
 * and provides methods for executing buy and sell transactions on behalf of players.
 * Each week, stock prices can be advanced with random fluctuations to simulate
 * market behaviour.</p>
 */
public class Exchange {
    private final String name;
    private int week;
    private final Map<String, Stock> stockMap;
    private final Random random;

    /**
     * Creates a new exchange with the given name and listed stocks.
     *
     * @param name   the name of the exchange; must not be {@code null} or blank
     * @param stocks the initial list of stocks to be listed on the exchange;
     *                must not be {@code null}, empty, or contain {@code null} elements
     * @throws IllegalArgumentException if {@code name} is {@code null} or blank,
     *                                  if {@code stocks} is {@code null} or empty,
     *                                  or if any stock in the list is {@code null}
     */
    public Exchange(String name, List<Stock> stocks) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (stocks == null || stocks.isEmpty()) {
            throw new IllegalArgumentException("Stocks list cannot be null or empty");
        }
        
        this.name = name.trim();
        this.week = 1;
        this.stockMap = new HashMap<>();
        this.random = new Random();
        
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
     * Checks whether a stock with the given symbol is listed on this exchange.
     *
     * @param symbol the ticker symbol to look up; must not be {@code null} or blank
     * @return {@code true} if a stock with the given symbol exists on this exchange,
     *         {@code false} otherwise
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
     * @return the {@link Stock} with the matching symbol, or {@code null} if no
     *         stock with that symbol is listed on this exchange
     * @throws IllegalArgumentException if {@code symbol} is {@code null} or blank
     */
    public Stock getStock(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        return stockMap.get(symbol);
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
     * Executes a buy transaction for the specified stock on behalf of a player.
     *
     * <p>A new {@link Share} is created at the stock's current sales price and the
     * resulting {@link Purchase} transaction is committed against the player's account.</p>
     *
     * @param symbol   the ticker symbol of the stock to buy; must not be {@code null} or blank
     * @param quantity the number of shares to buy; must be positive
     * @param player   the player executing the purchase; must not be {@code null}
     * @return the committed {@link Transaction} representing the purchase
     * @throws IllegalArgumentException if {@code symbol} is {@code null} or blank,
     *                                  if {@code quantity} is {@code null} or not positive,
     *                                  if {@code player} is {@code null},
     *                                  or if no stock with the given symbol is listed
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
            throw new IllegalArgumentException("Stock with symbol '" + symbol + "' not found on this exchange");
        }
        
        Share share = new Share(stock, quantity, stock.getSalesPrice());
        Purchase purchase = new Purchase(share, week);
        purchase.commit(player);
        return purchase;
    }

    /**
     * Executes a sell transaction for the given share on behalf of a player.
     *
     * <p>The share's stock must be listed on this exchange. A {@link Sale} transaction
     * is created and committed against the player's account.</p>
     *
     * @param share  the share to sell; must not be {@code null} and its stock must be
     *               listed on this exchange
     * @param player the player executing the sale; must not be {@code null}
     * @return the committed {@link Transaction} representing the sale
     * @throws IllegalArgumentException if {@code share} is {@code null},
     *                                  if {@code player} is {@code null},
     *                                  or if the share's stock is not listed on this exchange
     */
    public Transaction sell(Share share, Player player) {
        if (share == null) {
            throw new IllegalArgumentException("Share cannot be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        
        if (!hasStock(share.getStock().getSymbol())) {
            throw new IllegalArgumentException("Stock is not listed on this exchange");
        }
        
        Sale sale = new Sale(share, week);
        sale.commit(player);
        return sale;
    }

    /**
     * Advances the exchange by one week, updating all stock prices with random fluctuations.
     *
     * <p>Each stock's price is adjusted by a random percentage in the range
     * of &minus;10&nbsp;% to +10&nbsp;%. If the resulting price would be zero or negative,
     * the price is instead set to 50&nbsp;% of the current price to prevent
     * non-positive values.</p>
     */
    public void advance() {
        week++;
        
        for (Stock stock : stockMap.values()) {
            BigDecimal currentPrice = stock.getSalesPrice();
            
            double changePercent = (random.nextDouble() * 0.20) - 0.10;
            BigDecimal change = currentPrice.multiply(BigDecimal.valueOf(changePercent));
            BigDecimal newPrice = currentPrice.add(change);
            
            if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                newPrice = currentPrice.multiply(BigDecimal.valueOf(0.5));
            }
            
            stock.addNewSalesPrice(newPrice);
        }
    }
} 
