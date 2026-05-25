package edu.ntnu.idi.idatt2003.observer;

/**
 * Listener for changes in the game session.
 */
public interface GameObserver {

  /**
   * Called when the game state changes.
   */
  void onGameStateChanged();
}