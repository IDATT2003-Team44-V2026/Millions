package edu.ntnu.idi.idatt2003.service;

import edu.ntnu.idi.idatt2003.logic.Exchange;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Share;
import java.math.BigDecimal;
import edu.ntnu.idi.idatt2003.observer.GameObserver;
import java.util.ArrayList;
import java.util.List;

public class GameSession {

  private final Player player;
  private final Exchange exchange;
  private final List<GameObserver> observers;
  private boolean active;

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

  public void addObserver(GameObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Observer cannot be null");
    }
    observers.add(observer);
  }

  public void removeObserver(GameObserver observer) {
    observers.remove(observer);
  }

  private void notifyObservers() {
    for (GameObserver observer : observers) {
      observer.onGameStateChanged();
    }
  }

  public void buy(String symbol, java.math.BigDecimal quantity) {
    exchange.buy(symbol, quantity, player);
    notifyObservers();
  }

  public void sell(Share share) {
    exchange.sell(share, player);
    notifyObservers();
  }

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

  public void sellAll() {
    List<Share> shares = new ArrayList<>(player.getPortfolio().getShares());
    for (Share share : shares) {
      exchange.sell(share, player);
    }
    notifyObservers();
  }

  public void advanceWeek() {
    exchange.advance();
    notifyObservers();
  }

  public void endSession() {
    this.active = false;
    notifyObservers();
  }
}
