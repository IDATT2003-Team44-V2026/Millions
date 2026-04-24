package edu.ntnu.idi.idatt2003.ui.topbar;

import javafx.fxml.FXML;

/**
 * Controller for the application's top navigation bar.
 *
 * <p>The top bar provides global actions such as opening the sidebar menu. It does not directly
 * manipulate UI layout but delegates actions to the main controller.
 */
public class TopBarController {

  private Runnable onMenuToggle;

  /**
   * Registers an action to be executed when the hamburger menu button is clicked.
   *
   * @param action callback executed when the menu button is pressed
   */
  @FXML
  public void setOnMenuToggle(Runnable action) {
    this.onMenuToggle = action;
  }

  /**
   * Handles user interaction with the hamburger menu button.
   *
   * <p>Delegates the action to the registered callback in the main controller.
   */
  @FXML
  private void onMenuClicked() {
    if (onMenuToggle != null) {
      onMenuToggle.run();
    }
  }
}
