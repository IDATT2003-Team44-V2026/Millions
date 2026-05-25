package edu.ntnu.idi.idatt2003.service;

import edu.ntnu.idi.idatt2003.logic.Exchange;
import edu.ntnu.idi.idatt2003.model.Player;
import edu.ntnu.idi.idatt2003.model.Stock;
import java.math.BigDecimal;
import java.util.List;

/**
 * Factory for creating game sessions from new game setup data.
 */
public class GameSessionFactory {

  private static final String DEFAULT_EXCHANGE_NAME = "Market";

  /**
   * Creates a new game session.
   *
   * @param playerName      the player's name
   * @param startingCapital the player's starting capital
   * @param stocks          the stocks listed on the exchange
   * @return a new game session
   */
  public GameSession create(String playerName, BigDecimal startingCapital, List<Stock> stocks) {
    Player player = new Player(playerName, startingCapital);
    Exchange exchange = new Exchange(DEFAULT_EXCHANGE_NAME, stocks);
    return new GameSession(player, exchange);
  }
}
