package edu.ntnu.idi.idatt2003.view.components;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Helpers for start and setup form layouts.
 */
public final class FormLayouts {

  private FormLayouts() {
  }

  /**
   * Creates a labeled form field group.
   *
   * @param labelText the field label
   * @param control   the input control
   * @return the field group container
   */
  public static VBox fieldGroup(String labelText, Node control) {
    Label label = new Label(labelText);
    label.getStyleClass().add("field-label");
    label.setLabelFor(control);

    VBox fieldGroup = new VBox(6, label, control);
    fieldGroup.getStyleClass().add("field-group");
    return fieldGroup;
  }

  /**
   * Creates a menu button for start screens.
   *
   * @param text    the button text
   * @param primary whether to use primary styling
   * @return the button
   */
  public static Button menuButton(String text, boolean primary) {
    Button button = new Button(text);
    button.getStyleClass().addAll(primary ? "primary-button" : "secondary-button", "form-width");
    return button;
  }
}
