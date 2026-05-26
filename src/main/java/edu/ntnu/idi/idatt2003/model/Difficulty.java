package edu.ntnu.idi.idatt2003.model;

/**
 * Represents the market difficulty level, controlling drift and volatility used in price simulation.
 */
public enum Difficulty {
  EASY(0.12, 0.10),
  NORMAL(0.08, 0.20),
  HARD(0.02, 0.35);

  public final double drift;
  public final double volatility;

  Difficulty(double drift, double volatility) {
    this.drift = drift;
    this.volatility = volatility;
  }
}
