package edu.ntnu.idi.idatt2003.view.components;

import javafx.scene.control.Label;

/**
 * Creates styled placeholders for empty tables.
 */
public final class TablePlaceholders {

  private TablePlaceholders() {
  }

  /**
   * Creates a placeholder label for a table with no rows to show.
   *
   * @param message the message to display
   * @return the placeholder node
   */
  public static Label create(String message) {
    Label placeholder = new Label(message);
    placeholder.getStyleClass().add("empty-state");
    return placeholder;
  }
}
