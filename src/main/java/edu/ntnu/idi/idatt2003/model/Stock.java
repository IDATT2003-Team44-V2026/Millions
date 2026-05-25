package edu.ntnu.idi.idatt2003.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stock listed on an exchange.
 *
 * <p>A stock is identified by a unique ticker symbol and an associated company name.
 * It maintains a chronological history of sales prices, where the most recent entry is considered
 * the current sales price.</p>
 */
public class Stock {

  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  /**
   * Creates a new stock with the given symbol, company name, and initial sales price.
   *
   * @param symbol     the ticker symbol for the stock; must not be {@code null} or blank
   * @param company    the name of the company; must not be {@code null} or blank
   * @param salesPrice the initial sales price; must not be {@code null} and must be positive
   * @throws IllegalArgumentException if {@code symbol} is {@code null} or blank, if {@code company}
   *                                  is {@code null} or blank, or if {@code salesPrice} is
   *                                  {@code null} or not positive
   */
  public Stock(String symbol, String company, BigDecimal salesPrice) {
    if (symbol == null || symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Symbol cannot be null or empty");
    }
    if (company == null || company.trim().isEmpty()) {
      throw new IllegalArgumentException("Company name cannot be null or empty");
    }
    if (salesPrice == null) {
      throw new IllegalArgumentException("Sales price cannot be null");
    }
    if (salesPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Sales price must be positive");
    }

    this.symbol = symbol.trim();
    this.company = company.trim();
    this.prices = new ArrayList<>();
    this.prices.add(salesPrice);
  }

  /**
   * Returns the ticker symbol of this stock.
   *
   * @return the ticker symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Returns the company name associated with this stock.
   *
   * @return the company name
   */
  public String getCompany() {
    return company;
  }

  /**
   * Returns the current (most recent) sales price of this stock.
   *
   * @return the latest sales price, or {@code null} if the price history is empty
   */
  public BigDecimal getSalesPrice() {
    if (prices.isEmpty()) {
      return null;
    }
    return prices.getLast();
  }

  /**
   * Returns all registered sales prices for this stock.
   *
   * <p>The returned list is a defensive copy of the internal price history.
   * Modifying the returned list does not affect this stock.</p>
   *
   * @return a list of all registered sales prices in chronological order
   */
  public List<BigDecimal> getHistoricalPrices() {
    return new ArrayList<>(prices);
  }

  /**
   * Returns the highest registered sales price for this stock.
   *
   * @return the highest registered sales price, or {@code null} if no prices exist
   */
  public BigDecimal getHighestPrice() {
    if (prices.isEmpty()) {
      return null;
    }
    return prices.stream().max(BigDecimal::compareTo).orElse(null);
  }

  /**
   * Returns the lowest registered sales price for this stock.
   *
   * @return the lowest registered sales price, or {@code null} if no prices exist
   */
  public BigDecimal getLowestPrice() {
    if (prices.isEmpty()) {
      return null;
    }
    return prices.stream().min(BigDecimal::compareTo).orElse(null);
  }

  /**
   * Returns the change between the latest and previous registered sales price.
   *
   * <p>If only one price has been registered, the stock is considered to have
   * no price change and {@link BigDecimal#ZERO} is returned.</p>
   *
   * @return the difference between the latest and previous sales price
   */
  public BigDecimal getLatestPriceChange() {
    if (prices.size() < 2) {
      return BigDecimal.ZERO;
    }

    BigDecimal latestPrice = prices.get(prices.size() - 1);
    BigDecimal previousPrice = prices.get(prices.size() - 2);
    return latestPrice.subtract(previousPrice);
  }

  /**
   * Adds a new sales price to the stock's price history.
   *
   * <p>The new price becomes the current sales price returned by
   * {@link #getSalesPrice()}.</p>
   *
   * @param price the new sales price to add; must not be {@code null} and must be positive
   * @throws IllegalArgumentException if {@code price} is {@code null} or not positive
   */
  public void addNewSalesPrice(BigDecimal price) {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Price must be positive");
    }
    this.prices.add(price);
  }
}
