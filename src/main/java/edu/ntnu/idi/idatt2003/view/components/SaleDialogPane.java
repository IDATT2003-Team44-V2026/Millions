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
 * Dumb modal layout for confirming a share sale.
 */
public final class SaleDialogPane {

  private final VBox root;
  private final Label titleLabel;
  private final Label subtitleLabel;
  private final TextField quantityField;
  private final Label grossValueLabel;
  private final Label commissionValue;
  private final Label taxValue;
  private final Label totalProceedsValue;
  private final Label errorLabel;

  /**
   * Creates a sale dialog pane.
   *
   * @param onCancel  action when cancel is pressed
   * @param onConfirm action when confirm is pressed
   */
  public SaleDialogPane(Runnable onCancel, Runnable onConfirm) {
    titleLabel = new Label();
    titleLabel.getStyleClass().add("modal-title");

    subtitleLabel = new Label();
    subtitleLabel.getStyleClass().add("modal-subtitle");
    subtitleLabel.setWrapText(true);

    VBox heading = new VBox(4, titleLabel, subtitleLabel);
    heading.getStyleClass().add("modal-heading");

    quantityField = new TextField();
    quantityField.setPromptText("e.g. 5");
    quantityField.getStyleClass().add("form-input");

    grossValueLabel = new Label("-");
    commissionValue = new Label("-");
    taxValue = new Label("-");
    totalProceedsValue = new Label("-");

    HBox grossRow = SummaryRows.row("Gross value", grossValueLabel);
    HBox commissionRow = SummaryRows.row("Commission (1 %)", commissionValue);
    HBox taxRow = SummaryRows.row("Tax (30 % of profit)", taxValue);
    HBox totalRow = SummaryRows.row("Total proceeds", totalProceedsValue);
    totalRow.getStyleClass().add("summary-row-total");

    final VBox summaryBox = SummaryRows.card(grossRow, commissionRow, taxRow, totalRow);

    errorLabel = new Label();
    errorLabel.getStyleClass().add("error-label");
    errorLabel.setWrapText(true);

    Button cancelButton = new Button("Cancel");
    cancelButton.getStyleClass().add("secondary-button");
    cancelButton.setMinWidth(150);
    cancelButton.setOnAction(event -> onCancel.run());

    Button confirmButton = new Button("Confirm sale");
    confirmButton.getStyleClass().add("primary-button");
    confirmButton.setMinWidth(170);
    confirmButton.setOnAction(event -> onConfirm.run());

    HBox actions = new HBox(12, cancelButton, confirmButton);
    actions.getStyleClass().add("modal-actions");
    actions.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(16, heading, FormLayouts.fieldGroup("Quantity", quantityField), summaryBox,
        errorLabel, actions);
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
   * Resets the quantity field to the given value and requests focus.
   *
   * @param value the value to pre-fill
   */
  public void resetQuantity(String value) {
    quantityField.setText(value);
  }

  /**
   * Resets all preview labels to their placeholder state.
   */
  public void showInvalidPreview() {
    grossValueLabel.setText("-");
    commissionValue.setText("-");
    taxValue.setText("-");
    totalProceedsValue.setText("-");
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
   * Updates the sale proceeds preview labels.
   *
   * @param grossValue    formatted gross value
   * @param commission    formatted commission
   * @param tax           formatted tax
   * @param totalProceeds formatted total proceeds
   */
  public void setPreview(String grossValue, String commission, String tax, String totalProceeds) {
    grossValueLabel.setText(grossValue);
    commissionValue.setText(commission);
    taxValue.setText(tax);
    totalProceedsValue.setText(totalProceeds);
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
