package edu.ntnu.idi.idatt2003.service;

import edu.ntnu.idi.idatt2003.logic.Exchange;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import edu.ntnu.idi.idatt2003.observer.GameObserver;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages an active game session, coordinating actions between a {@link Player} and an
 * {@link Exchange}.
 *
 * <p>Observers can be registered to receive notifications whenever the game state changes.</p>
 */
public class GameSession {

  private final Player player;
  private final Exchange exchange;
  private final List<GameObserver> observers;
  private boolean active;

  /**
   * Creates a new game session with the given player and exchange.
   *
   * @param player   the player participating in the session; must not be {@code null}
   * @param exchange the exchange the player will trade on; must not be {@code null}
   * @throws IllegalArgumentException if either {@code player} or {@code exchange} is {@code null}
   */
  public GameSession(Player player, Exchange exchange) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (exchange == null) {
      throw new IllegalArgumentException("Exchange cannot be null");
    }

    this.player = player;
    this.exchange = exchange;
    this.observers = new ArrayList<>();
    this.active = true;
  }

  public Player getPlayer() {
    return player;
  }

  public Exchange getExchange() {
    return exchange;
  }

  public boolean isActive() {
    return active;
  }

  /**
   * Registers an observer to be notified on game state changes.
   *
   * @param observer the observer to add; must not be {@code null}
   * @throws IllegalArgumentException if {@code observer} is {@code null}
   */
  public void addObserver(GameObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Observer cannot be null");
    }
    observers.add(observer);
  }

  /**
   * Removes a previously registered observer.
   *
   * @param observer the observer to remove
   */
  public void removeObserver(GameObserver observer) {
    observers.remove(observer);
  }

  private void notifyObservers() {
    for (GameObserver observer : observers) {
      observer.onGameStateChanged();
    }
  }

  /**
   * Buys shares of the stock with the given symbol on behalf of the player.
   *
   * @param symbol   the ticker symbol of the stock to buy
   * @param quantity the number of shares to purchase
   */
  public void buy(String symbol, java.math.BigDecimal quantity) {
    exchange.buy(symbol, quantity, player);
    notifyObservers();
  }

  /**
   * Sells the given share on behalf of the player.
   *
   * @param share the share to sell
   */
  public void sell(Share share) {
    exchange.sell(share, player);
    notifyObservers();
  }

  /**
   * Sells a partial quantity of an owned share, keeping the remainder in the portfolio.
   *
   * @param original the share to partially sell; must be in the portfolio
   * @param qty      the quantity to sell; must be positive and not exceed the owned quantity
   * @throws IllegalArgumentException if {@code qty} is {@code null}, not positive, or exceeds
   *                                  the owned amount
   */
  public void sellPartial(Share original, BigDecimal qty) {
    if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (qty.compareTo(original.quantity()) > 0) {
      throw new IllegalArgumentException("Quantity exceeds owned amount");
    }
    Share soldPortion = new Share(original.stock(), qty, original.purchasePrice());
    BigDecimal remaining = original.quantity().subtract(qty);
    player.getPortfolio().removeShare(original);
    player.getPortfolio().addShare(soldPortion);
    if (remaining.compareTo(BigDecimal.ZERO) > 0) {
      player.getPortfolio().addShare(
          new Share(original.stock(), remaining, original.purchasePrice()));
    }
    exchange.sell(soldPortion, player);
    notifyObservers();
  }

  /**
   * Sells all shares currently held in the player's portfolio.
   */
  public void sellAll() {
    List<Share> shares = new ArrayList<>(player.getPortfolio().getShares());
    for (Share share : shares) {
      exchange.sell(share, player);
    }
    notifyObservers();
  }

  /**
   * Advances the exchange by one week, updating all stock prices.
   */
  public void advanceWeek() {
    exchange.advance();
    notifyObservers();
  }

  /**
   * Ends the game session and notifies all observers.
   */
  public void endSession() {
    this.active = false;
    notifyObservers();
  }
}
