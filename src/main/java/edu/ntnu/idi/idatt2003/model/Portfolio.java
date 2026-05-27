package edu.ntnu.idi.idatt2003.model;

import edu.ntnu.idi.idatt2003.calculators.SaleCalculator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's portfolio of share holdings.
 *
 * <p>The portfolio stores a collection of {@link Share} objects and provides
 * methods for adding, removing, retrieving, and checking for shares.</p>
 */
public class Portfolio {

  private final List<Share> shares;

  /**
   * Creates a new, empty portfolio.
   */
  public Portfolio() {
    this.shares = new ArrayList<>();
  }

  /**
   * Adds a share to this portfolio.
   *
   * @param share the share to add; must not be {@code null}
   * @return {@code true} if the share was successfully added
   * @throws IllegalArgumentException if {@code share} is {@code null}
   */
  public boolean addShare(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.add(share);
  }

  /**
   * Removes a share from this portfolio.
   *
   * @param share the share to remove; must not be {@code null}
   * @return {@code true} if the share was found and removed, {@code false} otherwise
   * @throws IllegalArgumentException if {@code share} is {@code null}
   */
  public boolean removeShare(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.remove(share);
  }

  /**
   * Returns a copy of all shares in this portfolio.
   *
   * <p>The returned list is a defensive copy; modifications to it
   * do not affect the portfolio's internal state.</p>
   *
   * @return a list of all shares in the portfolio
   */
  public List<Share> getShares() {
    return new ArrayList<>(shares);
  }

  /**
   * Returns all shares in this portfolio that belong to the stock with the given ticker symbol
   * (case-insensitive).
   *
   * @param symbol the ticker symbol to filter by; must not be {@code null} or blank
   * @return a list of shares whose stock matches the given symbol
   * @throws IllegalArgumentException if {@code symbol} is {@code null} or blank
   */
  public List<Share> getShares(String symbol) {
    if (symbol == null || symbol.trim().isEmpty()) {
      throw new IllegalArgumentException("Symbol cannot be null or empty");
    }
    List<Share> filteredShares = new ArrayList<>();
    for (Share share : shares) {
      if (share.stock().getSymbol().equalsIgnoreCase(symbol)) {
        filteredShares.add(share);
      }
    }
    return filteredShares;
  }

  /**
   * Returns the total net sale value of all shares in this portfolio.
   *
   * <p>Each share is valued using a {@link SaleCalculator}, so the returned
   * amount reflects the total value the player would receive if all holdings were sold at their
   * current prices.</p>
   *
   * @return the total net worth of this portfolio
   */
  public BigDecimal getNetWorth() {
    BigDecimal netWorth = BigDecimal.ZERO;
    for (Share share : shares) {
      netWorth = netWorth.add(new SaleCalculator(share).calculateTotal());
    }
    return netWorth;
  }

  /**
   * Checks whether this portfolio contains the given share.
   *
   * @param share the share to check for; must not be {@code null}
   * @return {@code true} if the portfolio contains the share, {@code false} otherwise
   * @throws IllegalArgumentException if {@code share} is {@code null}
   */
  public boolean contains(Share share) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    return shares.contains(share);
  }
}
