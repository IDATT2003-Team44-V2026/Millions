package edu.ntnu.idi.idatt2003.model;

/**
 * Thrown when a withdrawal is attempted that exceeds the player's current balance.
 */
public class InsufficientFundsException extends RuntimeException {

  public InsufficientFundsException(String message) {
    super(message);
  }
}
