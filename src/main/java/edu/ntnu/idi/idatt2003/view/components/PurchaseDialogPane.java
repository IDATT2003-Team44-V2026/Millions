package edu.ntnu.idi.idatt2003.view.components;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Dumb modal layout for confirming a stock purchase.
 */
public final class PurchaseDialogPane {

  private final VBox root;
  private final Label titleLabel;
  private final Label subtitleLabel;
  private final TextField quantityField;
  private final Label grossCostValue;
  private final Label commissionValue;
  private final Label totalCostValue;
  private final Label errorLabel;

  /**
   * Creates a purchase dialog pane.
   *
   * @param onCancel  action when cancel is pressed
   * @param onConfirm action when confirm is pressed
   */
  public PurchaseDialogPane(Runnable onCancel, Runnable onConfirm) {
    titleLabel = new Label();
    titleLabel.getStyleClass().add("modal-title");

    subtitleLabel = new Label();
    subtitleLabel.getStyleClass().add("modal-subtitle");
    subtitleLabel.setWrapText(true);

    VBox heading = new VBox(4, titleLabel, subtitleLabel);
    heading.getStyleClass().add("modal-heading");

    quantityField = new TextField("1");
    quantityField.getStyleClass().add("form-input");
    quantityField.setMaxWidth(Double.MAX_VALUE);

    final VBox quantityGroup = FormLayouts.fieldGroup("Quantity", quantityField);

    grossCostValue = new Label("-");
    commissionValue = new Label("-");
    totalCostValue = new Label("-");

    HBox grossCostRow = SummaryRows.row("Gross cost", grossCostValue);
    HBox commissionRow = SummaryRows.row("Commission (0.5 %)", commissionValue);
    HBox totalCostRow = SummaryRows.row("Total cost", totalCostValue);
    totalCostRow.getStyleClass().add("summary-row-total");

    final VBox summaryBox = SummaryRows.card(grossCostRow, commissionRow, totalCostRow);

    errorLabel = new Label();
    errorLabel.getStyleClass().add("error-label");
    errorLabel.setWrapText(true);

    Button cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().add("secondary-button");
    cancelButton.setMinWidth(150);
    cancelButton.setOnAction(event -> onCancel.run());

    Button confirmButton = new Button("Confirm purchase");
    confirmButton.getStyleClass().add("primary-button");
    confirmButton.setMinWidth(170);
    confirmButton.setOnAction(event -> onConfirm.run());

    HBox actions = new HBox(12, cancelButton, confirmButton);
    actions.getStyleClass().add("modal-actions");
    actions.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(16, heading, quantityGroup, summaryBox, errorLabel, actions);
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
   * Returns the quantity input field.
   *
   * @return the quantity field
   */
  public TextField getQuantityField() {
    return quantityField;
  }

  /**
   * Sets the dialog heading text.
   *
   * @param title    the title text
   * @param subtitle the subtitle text
   */
  public void setHeading(String title, String subtitle) {
    titleLabel.setText(title);
    subtitleLabel.setText(subtitle);
  }

  /**
   * Updates the purchase cost preview labels.
   *
   * @param grossCost  formatted gross cost
   * @param commission formatted commission
   * @param totalCost  formatted total cost
   */
  public void setPreview(String grossCost, String commission, String totalCost) {
    grossCostValue.setText(grossCost);
    commissionValue.setText(commission);
    totalCostValue.setText(totalCost);
  }

  /**
   * Shows invalid preview values.
   */
  public void showInvalidPreview() {
    grossCostValue.setText("-");
    commissionValue.setText("-");
    totalCostValue.setText("-");
  }

  /**
   * Resets the quantity field to the default value.
   */
  public void resetQuantity() {
    quantityField.setText("1");
  }

  /**
   * Shows an error message in the dialog.
   *
   * @param message the error message
   */
  public void showError(String message) {
    errorLabel.setText(message);
  }

  /**
   * Clears any error message in the dialog.
   */
  public void clearError() {
    errorLabel.setText("");
  }
}
