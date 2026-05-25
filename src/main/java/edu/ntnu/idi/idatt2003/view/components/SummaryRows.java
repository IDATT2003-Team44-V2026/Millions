package edu.ntnu.idi.idatt2003.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Helpers for modal summary rows and cards.
 */
public final class SummaryRows {

  private SummaryRows() {
  }

  /**
   * Creates a summary row with a label and value.
   *
   * @param labelText  the row label
   * @param valueLabel the value label (updated by the parent view)
   * @return the row container
   */
  public static HBox row(String labelText, Label valueLabel) {
    Label label = new Label(labelText);
    label.getStyleClass().add("summary-label");
    valueLabel.getStyleClass().add("summary-value");

    HBox summaryRow = new HBox(16, label, valueLabel);
    summaryRow.getStyleClass().add("summary-row");
    summaryRow.setAlignment(Pos.CENTER_LEFT);
    summaryRow.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(label, Priority.ALWAYS);
    valueLabel.setAlignment(Pos.CENTER_RIGHT);
    return summaryRow;
  }

  /**
   * Creates a summary card containing the given rows.
   *
   * @param rows the summary rows to include
   * @return the summary card container
   */
  public static VBox card(HBox... rows) {
    VBox summaryCard = new VBox(6, rows);
    summaryCard.getStyleClass().add("summary-card");
    return summaryCard;
  }
}
