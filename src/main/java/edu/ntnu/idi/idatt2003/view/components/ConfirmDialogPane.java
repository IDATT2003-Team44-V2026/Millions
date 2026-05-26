package edu.ntnu.idi.idatt2003.view.components;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Generic confirm / cancel modal card.
 */
public final class ConfirmDialogPane {

  private final VBox root;
  private final Label titleLabel;
  private final Label bodyLabel;

  /**
   * Creates a confirm dialog pane.
   *
   * @param onCancel  action invoked when Cancel is pressed
   * @param onConfirm action invoked when Confirm is pressed
   */
  public ConfirmDialogPane(Runnable onCancel, Runnable onConfirm) {
    titleLabel = new Label();
    titleLabel.getStyleClass().add("modal-title");

    bodyLabel = new Label();
    bodyLabel.getStyleClass().add("modal-subtitle");
    bodyLabel.setWrapText(true);

    VBox heading = new VBox(4, titleLabel, bodyLabel);
    heading.getStyleClass().add("modal-heading");

    Button cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().add("secondary-button");
    cancelButton.setMinWidth(120);
    cancelButton.setOnAction(e -> onCancel.run());

    Button confirmButton = new Button("Confirm");
    confirmButton.getStyleClass().add("primary-button");
    confirmButton.setMinWidth(150);
    confirmButton.setOnAction(e -> onConfirm.run());

    HBox actions = new HBox(12, cancelButton, confirmButton);
    actions.getStyleClass().add("modal-actions");
    actions.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(16, heading, actions);
    root.getStyleClass().add("modal-card");
    root.setMaxSize(430, Region.USE_PREF_SIZE);
  }

  /**
   * Returns the modal root node.
   *
   * @return the root node
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Sets the dialog title and body text.
   *
   * @param title the title line
   * @param body  the explanatory body text
   */
  public void setContent(String title, String body) {
    titleLabel.setText(title);
    bodyLabel.setText(body);
  }
}
