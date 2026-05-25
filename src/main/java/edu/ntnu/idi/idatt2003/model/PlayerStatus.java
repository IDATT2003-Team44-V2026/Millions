package edu.ntnu.idi.idatt2003.model;

/**
 * Represents the player's progression level in the stock-trading game.
 */
public enum PlayerStatus {
  NOVICE,
  INVESTOR,
  SPECULATOR;

  /**
   * Returns a user-facing label for this status.
   *
   * @return the display label
   */
  public String getDisplayName() {
    return switch (this) {
      case NOVICE -> "Novice";
      case INVESTOR -> "Investor";
      case SPECULATOR -> "Speculator";
    };
  }
}
