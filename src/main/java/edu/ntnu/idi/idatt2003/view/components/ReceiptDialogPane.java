package edu.ntnu.idi.idatt2003.view.components;

import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Read-only receipt modal shown after a completed purchase or sale.
 */
public final class ReceiptDialogPane {

  /**
   * A single line item on the receipt.
   *
   * @param label the row description
   * @param value the formatted value
   * @param total whether this row is the grand-total line (rendered in bold)
   */
  public record ReceiptRow(String label, String value, boolean total) {}

  private final VBox root;
  private final Label titleLabel;
  private final Label subtitleLabel;
  private final VBox summaryCard;

  /**
   * Creates a receipt dialog pane.
   *
   * @param onClose action invoked when the Done button is pressed
   */
  public ReceiptDialogPane(Runnable onClose) {
    Label badgeLabel = new Label("Transaction complete");
    badgeLabel.getStyleClass().add("receipt-badge");

    titleLabel = new Label();
    titleLabel.getStyleClass().add("modal-title");

    subtitleLabel = new Label();
    subtitleLabel.getStyleClass().add("modal-subtitle");
    subtitleLabel.setWrapText(true);

    VBox heading = new VBox(6, badgeLabel, titleLabel, subtitleLabel);
    heading.getStyleClass().add("modal-heading");

    Region divider = new Region();
    divider.getStyleClass().add("receipt-divider");

    summaryCard = new VBox(6);
    summaryCard.getStyleClass().add("summary-card");

    Button closeButton = new Button("Done");
    closeButton.getStyleClass().add("primary-button");
    closeButton.setMinWidth(120);
    closeButton.setOnAction(event -> onClose.run());

    HBox actions = new HBox(closeButton);
    actions.getStyleClass().add("modal-actions");
    actions.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(16, heading, divider, summaryCard, actions);
    root.getStyleClass().addAll("modal-card", "receipt-card");
    root.setMaxSize(400, Region.USE_PREF_SIZE);
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
   * Populates the receipt with content.
   *
   * @param title    the receipt title (e.g. "Purchase Receipt")
   * @param subtitle the transaction summary line (e.g. "AAPL · Apple Inc. · 5 share(s)")
   * @param rows     the line items to display
   */
  public void setContent(String title, String subtitle, List<ReceiptRow> rows) {
    titleLabel.setText(title);
    subtitleLabel.setText(subtitle);

    summaryCard.getChildren().clear();
    for (ReceiptRow row : rows) {
      Label valueLabel = new Label(row.value());
      HBox summaryRow = SummaryRows.row(row.label(), valueLabel);
      if (row.total()) {
        summaryRow.getStyleClass().add("summary-row-total");
      }
      summaryCard.getChildren().add(summaryRow);
    }
  }
}
