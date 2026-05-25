package edu.ntnu.idi.idatt2003.io;

import java.util.List;

/**
 * Data-transfer object for a persisted game session.
 */
public class GameSave {

  static final int CURRENT_VERSION = 1;

  int version = CURRENT_VERSION;
  String playerName;
  String savedAt;
  PlayerData player;
  ExchangeData exchange;


  static class PlayerData {

    String name;
    String startingMoney;
    String currentMoney;
    List<ShareData> portfolio;
    List<TransactionData> transactions;
  }

  static class ExchangeData {

    String name;
    int week;
    List<StockData> stocks;
  }

  static class StockData {

    String symbol;
    String company;
    List<String> priceHistory;
  }

  static class ShareData {

    String stockSymbol;
    String quantity;
    String purchasePrice;
  }

  static class TransactionData {

    String type;
    int week;
    String stockSymbol;
    String stockCompany;
    String quantity;
    String purchasePrice;
    String salesPrice;
  }

  /**
   * Returns the player name stored in this save.
   *
   * @return the player name
   */
  public String getPlayerName() {
    return playerName;
  }

  /**
   * Returns the formatted timestamp when this save was created.
   *
   * @return the save timestamp
   */
  public String getSavedAt() {
    return savedAt;
  }

  /**
   * Returns the exchange week stored in this save.
   *
   * @return the week number, or 0 if unavailable
   */
  public int getSavedWeek() {
    return exchange != null ? exchange.week : 0;
  }
}
