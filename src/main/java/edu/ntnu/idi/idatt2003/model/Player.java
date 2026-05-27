package edu.ntnu.idi.idatt2003.model;

import edu.ntnu.idi.idatt2003.transactions.TransactionArchive;
import java.math.BigDecimal;

/**
 * Represents a player in the stock-trading game.
 *
 * <p>A player has a name, a money balance, a {@link Portfolio} of share holdings,
 * and a {@link TransactionArchive} that records all completed transactions. The starting capital is
 * preserved separately so that the player's overall performance can be evaluated later.</p>
 */
public class Player {

  private final String name;
  private final BigDecimal startingMoney;
  private final Portfolio portfolio;
  private final TransactionArchive transactionArchive;
  private BigDecimal money;

  /**
   * Creates a new player with the given name and starting capital.
   *
   * <p>The player is initialised with an empty portfolio and an empty
   * transaction archive. The current money balance is set equal to the starting capital.</p>
   *
   * @param startingMoney the initial capital; must not be {@code null} or negative
   * @param name          the player's name; must not be {@code null} or blank
   * @throws IllegalArgumentException if {@code name} is {@code null} or blank, or if
   *                                  {@code startingMoney} is {@code null} or negative
   */
  public Player(String name, BigDecimal startingMoney) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (startingMoney == null) {
      throw new IllegalArgumentException("Starting money cannot be null");
    }
    if (startingMoney.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Starting money cannot be negative");
    }

    this.name = name.trim();
    this.startingMoney = startingMoney;
    this.money = startingMoney;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();
  }

  /**
   * Returns the player's name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the player's current money balance.
   *
   * @return the current balance
   */
  public BigDecimal getMoney() {
    return money;
  }

  /**
   * Returns the player's starting capital.
   *
   * @return the starting capital
   */
  public BigDecimal getStartingMoney() {
    return startingMoney;
  }

  /**
   * Adds the specified amount to the player's money balance.
   *
   * @param amount the amount to add; must not be {@code null} and must be positive
   * @throws IllegalArgumentException if {@code amount} is {@code null} or not positive
   */
  public void addMoney(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    this.money = this.money.add(amount);
  }

  /**
   * Withdraws the specified amount from the player's money balance.
   *
   * @param amount the amount to withdraw; must not be {@code null}, must be positive, and must not
   *               exceed the current balance
   * @throws IllegalArgumentException if {@code amount} is {@code null}, not positive, or greater
   *                                  than the current balance (insufficient funds)
   */
  public void withdrawMoney(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (this.money.compareTo(amount) < 0) {
      throw new InsufficientFundsException("Insufficient funds");
    }
    this.money = this.money.subtract(amount);
  }

  /**
   * Returns the player's portfolio of share holdings.
   *
   * @return the {@link Portfolio}
   */
  public Portfolio getPortfolio() {
    return portfolio;
  }

  /**
   * Returns the player's current total net worth.
   *
   * <p>The player's net worth is the sum of the remaining money balance and
   * the current net sale value of the portfolio.</p>
   *
   * @return the player's current net worth
   */
  public BigDecimal getNetWorth() {
    return money.add(portfolio.getNetWorth());
  }

  /**
   * Returns the player's current status level.
   *
   * <p>Status is determined by the player's current net worth compared with
   * the starting capital, and the number of distinct weeks in which the player has made at least
   * one transaction.</p>
   *
   * @return the player's current {@link PlayerStatus}
   */
  public PlayerStatus getStatus() {
    int weeksPlayed = transactionArchive.countDistinctWeeks();
    BigDecimal netWorth = getNetWorth();

    if (weeksPlayed >= 20
        && netWorth.compareTo(startingMoney) > 0
        && netWorth.compareTo(startingMoney.multiply(new BigDecimal("2.00"))) >= 0) {
      return PlayerStatus.SPECULATOR;
    }

    if (weeksPlayed >= 10
        && netWorth.compareTo(startingMoney) > 0
        && netWorth.compareTo(startingMoney.multiply(new BigDecimal("1.20"))) >= 0) {
      return PlayerStatus.INVESTOR;
    }

    return PlayerStatus.NOVICE;
  }

  /**
   * Returns the player's transaction archive containing all completed transactions.
   *
   * @return the {@link TransactionArchive}
   */
  public TransactionArchive getTransactionArchive() {
    return transactionArchive;
  }
}
